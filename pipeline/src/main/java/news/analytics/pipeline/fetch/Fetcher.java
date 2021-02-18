package news.analytics.pipeline.fetch;

import news.analytics.dao.core.GenericDao;
import news.analytics.model.constants.NewsAgency;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.model.news.RawNews;
import news.analytics.model.news.Seed;
import news.analytics.pipeline.utils.PipelineUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class Fetcher {
    private GenericDao<Seed> seedDao;
    private GenericDao<RawNews> rawNewsDao;

    public Fetcher() {
        seedDao = new GenericDao<>(Seed.class);
        rawNewsDao = new GenericDao<>(RawNews.class);
    }

    public RawNews fetch(Seed seed, Connection connection) {
        RawNews rawNews = null;
        try {
            // httpGet
            String rawHtml = httpGet(seed); // seed gets updated with httpGet status
            rawNews = prepareRawNewsInstance(rawHtml, seed.getUri());

            // insert RawNews only if fetched
            if(seed.getFetchStatus().equals(FetchStatus.FETCHED)) {
                insert(rawNews, connection);
                // update status of the seed to crawlDb
                update(connection, seed);
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Failed rollback:"+ e1);
            }
        }
        return rawNews;
    }

    private String httpGet(Seed seed) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL uri = new URL(seed.getUri());
        HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();

        String fetchStatus = FetchStatus.UNFETCHED;
        if(responseCode >= 400 && responseCode < 500) {
            fetchStatus = FetchStatus.CLIENT_ERROR;
        } else if(responseCode >= 300 && responseCode < 400) {
            fetchStatus = FetchStatus.REDIRECT;
        } else if(responseCode > 500) {
            fetchStatus = FetchStatus.SERVER_ERROR;
        } else if(responseCode >= 200 && responseCode < 300) { // usually 200 ok
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            fetchStatus = FetchStatus.FETCHED;
        }
        seed.setHttpCode((short) responseCode);
        seed.setFetchStatus(fetchStatus);

        return sb.toString();
    }

    private boolean update(Connection connection, Seed seed) throws SQLException {
        seedDao.update(connection, Arrays.asList(seed));
        return true;
    }

    private boolean insert(RawNews rawNews, Connection connection) throws SQLException, MalformedURLException {
        rawNewsDao.insert(connection, Arrays.asList(rawNews));
        return true;
    }

    private RawNews prepareRawNewsInstance(String rawHtml, String uri) throws MalformedURLException {
        RawNews rawNews = new RawNews();
        rawNews.setId(PipelineUtils.hashIt(uri));
        rawNews.setUri(uri);
        rawNews.setRawContent(rawHtml);
        rawNews.setNewsAgency(getNewsAgencyFromUri(uri));
        rawNews.setProcessStatus(ProcessStatus.RAW_NEWS_UNPROCESSED);
        return rawNews;
    }

    private String getNewsAgencyFromUri(String uri) throws MalformedURLException {
        String host = new URL(uri).getHost();
        NewsAgency newsAgency = NewsAgency.getNewsAgency(host);
        return newsAgency.getNewsAgency();
    }
}

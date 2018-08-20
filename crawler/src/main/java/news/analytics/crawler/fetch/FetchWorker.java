package news.analytics.crawler.fetch;

import com.google.common.collect.Lists;
import news.analytics.crawler.constants.FetchStatus;
import news.analytics.crawler.utils.CrawlerUtils;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;
import news.analytics.model.constants.NewsAgency;
import news.analytics.model.constants.ProcessStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FetchWorker extends Thread {
    private GenericDao<Seed> seedDao;
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<Seed> seedList;

    public FetchWorker(DataSource dataSource, GenericDao seedDao, GenericDao rawNewsDao, List<Seed> seedList) {
        this.dataSource = dataSource;
        this.seedDao = seedDao;
        this.rawNewsDao = rawNewsDao;
        this.seedList = seedList;
    }

    @Override
    public void run() {

        // Fetch -> Insert in getRawNews -> Update seed status
        for(Seed seed : seedList) {
            String rawHtml = null;
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                // fetch
                rawHtml = fetch(seed); // seed gets updated with fetch status

                // insert RawNews only if fetched
                if(seed.getFetchStatus().equals(FetchStatus.FETCHED)) {
                    insert(seed, rawHtml, connection);
                }

                // update status of the seed to cralDb
                update(connection, seed);

                connection.commit();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Rolling back.");
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException e1) {
                    System.out.println("Failed rollback:"+ e1);
                }
            } catch (SQLException e) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                    // log
                }
                throw new RuntimeException(e);
            }
        }
    }

    private String fetch(Seed seed) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL uri = new URL(seed.getUri());
        HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
//        connection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        connection.setRequestProperty("", "{'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',\n" +
//                "       'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',\n" +
//                "       'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',\n" +
//                "       'Accept-Encoding': 'none',\n" +
//                "       'Accept-Language': 'en-US,en;q=0.8',\n" +
//                "       'Connection': 'keep-alive'}");
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
        seedDao.update(connection, Lists.newArrayList(seed));
        return true;
    }

    private boolean insert(Seed seed, String rawHtml, Connection connection) throws SQLException, MalformedURLException {
        String uri = seed.getUri();
        RawNews rawNews = getRawNews(rawHtml, uri);
        rawNewsDao.insert(connection, Lists.newArrayList(rawNews));
        return true;
    }

    private RawNews getRawNews(String rawHtml, String uri) throws MalformedURLException {
        RawNews rawNews = new RawNews();
        rawNews.setId(CrawlerUtils.hashIt(uri));
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

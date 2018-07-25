package news.analytics.crawler.fetch;

import com.google.common.collect.Lists;
import news.analytics.crawler.constants.FetchStatus;
import news.analytics.crawler.utils.CrawlerUtils;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
            try {
                Connection connection = dataSource.getConnection();
                // fetch
                rawHtml = fetch(seed); // seed gets updated with fetch status

                // insert RawNews only if fetched
                if(seed.getFetchStatus().equals(FetchStatus.FETCHED)) {
                    insert(seed, rawHtml, connection);
                }

                update(connection, seed);
                connection.commit();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO failed url handling
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String fetch(Seed seed) throws IOException {
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
        seedDao.update(connection, Lists.newArrayList(seed));
        return true;
    }

    private boolean insert(Seed seed, String rawHtml, Connection connection) throws SQLException {
        String uri = seed.getUri();
        RawNews rawNews = getRawNews(rawHtml, uri);
        rawNewsDao.insert(connection, Lists.newArrayList(rawNews));
        return true;
    }

    private RawNews getRawNews(String rawHtml, String uri) {
        RawNews rawNews = new RawNews();
        rawNews.setId(CrawlerUtils.hashIt(uri));
        rawNews.setUri(uri);
        rawNews.setRawContent(rawHtml);
        rawNews.setNewsAgency(getNewsAgencyFromUri(uri));
        return rawNews;
    }

    private String getNewsAgencyFromUri(String uri) {
        return null;
    }
}

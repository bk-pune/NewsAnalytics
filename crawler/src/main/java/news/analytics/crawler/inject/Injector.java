package news.analytics.crawler.inject;

import news.analytics.crawler.utils.FileUtils;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.Seed;
import news.analytics.pipeline.fetch.FetchStatus;
import news.analytics.pipeline.utils.PipelineUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Injects the given URLs as Seeds in the database.
 */
public class Injector {
    private GenericDao genericDao;
    private DataSource dataSource;
    private Lock injectorFetcherLock;

    public Injector(DataSource dataSource, Lock injectorFetcherLock) {
        this.genericDao = new GenericDao(Seed.class);
        this.dataSource = dataSource;
        this.injectorFetcherLock = injectorFetcherLock;
    }

    public int inject(String seedFileName, String skipSeedsFileName) throws IOException, SQLException {
        int injectedCount = 0;
        int rejetedCount = 0;

        Connection connection = dataSource.getConnection();
        try {
            List<String> seeds = FileUtils.readFile(seedFileName);
            HashSet<String> skipSeeds = new HashSet<>(FileUtils.readFile(skipSeedsFileName));

            for(String seed : seeds) {
                // ignore comments and blank lines
                if(seed.startsWith("#") || seed.trim().isEmpty() || skipSeeds.contains(seed)) {
                    continue;
                }

                try {
                    genericDao.insert(connection, Arrays.asList(getFreshSeed(seed)));
                    connection.commit();
                    injectedCount++;
                } catch (SQLException e) {
//                  System.out.println(e);
                    rejetedCount++;
                }
            }
            System.out.println("\n\nSeeds Injected: "+ injectedCount);
            System.out.println("\nSeeds Rejected: "+ rejetedCount);
            System.out.println("--------------------------------------");
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            connection.close();
        }
        synchronized (injectorFetcherLock) {
            // notify fetcher thread
            injectorFetcherLock.notify();
        }
        return injectedCount;
    }

    private Seed getFreshSeed(String uri){
        Seed seed = new Seed();
        seed.setUri(uri);
        seed.setFetchStatus(FetchStatus.UNFETCHED);
        seed.setHttpCode((short) -1); // by default status code is -1
        seed.setId(PipelineUtils.hashIt(uri));
        return seed;
    }
}
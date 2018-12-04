package news.analytics.crawler.inject;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.Seed;
import news.analytics.pipeline.fetch.FetchStatus;
import news.analytics.pipeline.utils.PipelineUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

    public int inject(String fileName) throws IOException, SQLException {
        int injectedCount = 0;
        int rejetedCount = 0;
        Connection connection = dataSource.getConnection();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String nextLine;
            while ((nextLine = br.readLine()) != null){
                // ignore comments and blank lines
                if(nextLine.startsWith("#") || nextLine.trim().isEmpty()) {
                    continue;
                }
                try {
                    genericDao.insert(connection, Lists.newArrayList(getFreshSeed(nextLine)));
                    connection.commit();
                    injectedCount++;
                } catch (SQLException e) {
//                    System.out.println(e);
                    rejetedCount++;
                }
            }
            br.close();
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
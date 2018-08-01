package news.analytics.crawler.inject;

import com.google.common.collect.Lists;
import news.analytics.crawler.constants.FetchStatus;
import news.analytics.crawler.utils.CrawlerUtils;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.Seed;

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

    public Injector(DataSource dataSource) {
        this.genericDao = new GenericDao(Seed.class);
        this.dataSource = dataSource;
    }

    public int inject(String fileName) throws IOException, SQLException {
        int injectedCount = 0;
        int rejetedCount = 0;
        Connection connection = dataSource.getConnection();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String nextLine;
            while ((nextLine = br.readLine()) != null){
                try {
                    genericDao.insert(connection, Lists.newArrayList(getFreshSeed(nextLine)));
                    connection.commit();
                    injectedCount++;
                } catch (Exception e){
                    System.out.println(e);
                    rejetedCount++;
                };
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            connection.close();
        }
        return injectedCount;
    }

    private Seed getFreshSeed(String uri){
        Seed seed = new Seed();
        seed.setUri(uri);
        seed.setFetchStatus(FetchStatus.UNFETCHED);
        seed.setHttpCode((short) -1); // by default status code is -1
        seed.setId(CrawlerUtils.hashIt(uri));
        return seed;
    }
}
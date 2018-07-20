package news.analytics.crawler.inject;

import news.analytics.crawler.constants.FetchStatus;
import news.analytics.crawler.utils.CrawlerUtils;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.Seed;

import java.io.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String nextLine;
            List<Seed> seeds = new LinkedList<Seed>();
            while ((nextLine = br.readLine()) != null){
                Seed freshSeed = getFreshSeed(nextLine);
                seeds.add(freshSeed);
            }
            br.close();

            // generic dao.insert
            genericDao.insert(dataSource.getConnection(), seeds);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
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
package news.analytics.crawler.main;

import news.analytics.crawler.inject.Injector;
import news.analytics.crawler.stats.StatsProvider;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import static news.analytics.crawler.constants.CrawlerConstants.EXIT;
import static news.analytics.crawler.constants.CrawlerConstants.menuString;

public class Crawler {

    private final DataSource dataSource;
    private Injector injector;
    private StatsProvider statsProvider;

    private Crawler(String driverClass, String jdbcUrl, String userName, String password) {
        dataSource = H2DataSource.getDataSource(driverClass, jdbcUrl, userName, password);
        injector = new Injector(dataSource);
        statsProvider = new StatsProvider(dataSource);
    }

    public static void main(String[] args) throws IOException {
        Properties properties = loadProperties("config.properties"); // will be loaded from classpath
        Crawler crawler = new Crawler(properties.getProperty("driverClass"), properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"));
        System.out.println("Crawler initialized successfully.");
        String input = "";
        Scanner sc = new Scanner(System.in);
        while(! input.equalsIgnoreCase(EXIT) ) {
            try {
                showMenu();
                input = sc.nextLine();
                if(input.equalsIgnoreCase("1")) { // startInjector
                    System.out.println("Enter full file path for seed url:");
                    input = sc.nextLine();
                    crawler.startInjector(input);
                } else if(input.equalsIgnoreCase("2")) { // fetch
                    crawler.startFetcher();
                } else if(input.equalsIgnoreCase("3")) { // show stats
                    System.out.println("Enter predicate, leave empty for all results: "); // FETCH_STATUS = UNFETCHED
                    String predicateString = sc.nextLine();
                    crawler.showStats(predicateString);

                } else if(input.equalsIgnoreCase("0")) {
                    System.out.println("Good bye !");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    private void showStats(String predicateString) throws SQLException, IOException, InstantiationException, IllegalAccessException {
        String stats;
        if(predicateString == null || predicateString.trim().length() == 0){
            stats = statsProvider.getStats(null);
        } else {
            String[] split = predicateString.split("="); // FETCH_STATUS = UNFETCHED => [0]-> FETCH_STATUS [1] -> UNFETCHED
            PredicateClause predicateClause = new PredicateClause(split[0].trim(), PredicateOperator.EQUAL, split[1].trim());
            stats = statsProvider.getStats(predicateClause);
        }
        System.out.println(stats);
    }

    private void startFetcher() {
        // TODO
    }

    // TODO make it threaded so that injector and fetcher can run in parallel
    private int startInjector(String fileName) throws IOException, SQLException {
        System.out.println("Starting injector..");
        int injectedCount = injector.inject(fileName);
        System.out.println("Total seeds injected in crawlDb: "+injectedCount);
        return injectedCount;
    }

    private static void showMenu() {
        System.out.println(menuString);
    }

    private static Properties loadProperties(String propertiesFileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Injector.class.getClassLoader().getResourceAsStream(propertiesFileName);
        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found !");
        }
        return properties;
    }
}

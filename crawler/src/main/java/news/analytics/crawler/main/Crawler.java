package news.analytics.crawler.main;

import news.analytics.crawler.fetch.Fetcher;
import news.analytics.crawler.inject.Injector;
import news.analytics.crawler.stats.StatsProvider;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.pipeline.analyze.Analyzer;
import news.analytics.pipeline.transform.Transformer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import static news.analytics.crawler.constants.CrawlerConstants.EXIT;
import static news.analytics.crawler.constants.CrawlerConstants.menuString;
import static news.analytics.dao.query.QueryConstants.LIMIT;
import static news.analytics.dao.query.QueryConstants.SPACE;

public class Crawler {

    private final DataSource dataSource;
    private Injector injector;
    private StatsProvider statsProvider;
    private Fetcher fetcher;
    private Transformer transformer;
    private Analyzer analyzer;
    private Properties properties;

    private Crawler(String propertiesFilePath) throws IOException {
        properties = loadProperties(propertiesFilePath); // will be loaded from classpath
        dataSource = H2DataSource.getDataSource(properties.getProperty("driverClass"), properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"));
        injector = new Injector(dataSource);
        statsProvider = new StatsProvider(dataSource);
        fetcher = new Fetcher(dataSource);
        transformer = new Transformer(dataSource);
        analyzer = new Analyzer(dataSource);
    }

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler("config.properties");
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
                    System.out.println("Please enter fetch predicate. [Default FETCH_STATUS = UNFETCHED]: "); // FETCH_STATUS = UNFETCHED
                    String predicateString = sc.nextLine();
                    System.setProperty("http.agent", crawler.properties.getProperty("crawlerName"));
                    crawler.startFetcher(predicateString);
                } else if(input.equalsIgnoreCase("3")) { // start transformer
                    crawler.startTransformer();
                } else if(input.equalsIgnoreCase("4")) { // start analyzer
                    crawler.startAnalyzer();
                } else if(input.equalsIgnoreCase("5")) { // show stats
                    System.out.println("Enter predicate. [Default FETCH_STATUS = UNFETCHED]: "); // FETCH_STATUS = UNFETCHED
                    String predicateString = sc.nextLine();
                    if(predicateString == null || predicateString.trim().equals("")) {
                        predicateString = "FETCH_STATUS = UNFETCHED";
                    }
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

    private void showStats(String predicateString) throws Exception {
        String stats;
        if(predicateString == null || predicateString.trim().length() == 0) {
            predicateString = "FETCH_STATUS = UNFETCHED";
        }

        PredicateClause predicateClause = DAOUtils.getPredicateFromString(predicateString);
        stats = statsProvider.getStats(predicateClause);
        System.out.println(stats);
    }

    private void startFetcher(String predicateString) throws Exception {
        PredicateClause predicateClause = DAOUtils.getPredicateFromString(predicateString);
        predicateClause.setLimitClause(LIMIT + SPACE + properties.getProperty("limit"));
        fetcher.start(predicateClause, Integer.parseInt(properties.getProperty("fetcherThreads")));
    }

    // TODO make it threaded so that injector and fetcher can run in parallel
    private int startInjector(String fileName) throws IOException, SQLException {
        System.out.println("Starting injector..");
        int injectedCount = injector.inject(fileName);
        System.out.println("Total seeds injected in crawlDb: "+injectedCount);
        return injectedCount;
    }

    private void startTransformer() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Starting transformer...");
        transformer.transform(Integer.parseInt(properties.getProperty("processorThreads")));
        System.out.println("Transformer started successfully.");
    }

    private void startAnalyzer() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Starting analyzer...");
        analyzer.analyze(Integer.parseInt(properties.getProperty("processorThreads")));
        System.out.println("Analyzer started successfully.");
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

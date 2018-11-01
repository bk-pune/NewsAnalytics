package news.analytics.crawler.main;

import news.analytics.crawler.AnalyzerManager;
import news.analytics.crawler.fetchtransform.FetcherTransformerManager;
import news.analytics.crawler.inject.Injector;
import news.analytics.crawler.stats.StatsProvider;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.model.lock.Lock;

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
    private FetcherTransformerManager fetcherTransformerManager;
    private AnalyzerManager analyzerManager;
    private Properties properties;
    private int analyzerThreads;

    private Crawler(String propertiesFilePath) throws IOException {
        properties = loadProperties(propertiesFilePath); // will be loaded from classpath
        int fetcherTransformerThreads = Integer.parseInt(properties.getProperty("fetcherTransformerThreads"));
        analyzerThreads = Integer.parseInt(properties.getProperty("analyzerThreads"));

        System.setProperty("http.agent", properties.getProperty("crawlerName"));

        System.out.println("Properties initialized successfully.");

        dataSource = H2DataSource.getDataSource(properties.getProperty("driverClass"), properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"));
        System.out.println("Data source and connection pool initialized successfully.");

        Lock injectorFetcherLock = new Lock();

        injector = new Injector(dataSource, injectorFetcherLock);
        System.out.println("Injector initialized.");
        statsProvider = new StatsProvider(dataSource);

        fetcherTransformerManager = new FetcherTransformerManager(dataSource, fetcherTransformerThreads, injectorFetcherLock);
        fetcherTransformerManager.start();

        System.out.println("FetcherTransformerManager initialized.");
    }

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler("config.properties");
        System.out.println();
        System.out.println("Crawler initialized successfully.");

        String input = "";
        Scanner sc = new Scanner(System.in);
        while(! input.equalsIgnoreCase(EXIT) ) {
            try {
                showMenu();
                input = sc.nextLine();
                if(input.equalsIgnoreCase("1")) { // inject
                    System.out.println("Enter full file path for seed url:");
                    input = sc.nextLine();
                    crawler.inject(input);
                } else if(input.equalsIgnoreCase("2")) { // Analyze
                     crawler.startAnalyzer();
                } else if(input.equalsIgnoreCase("3")) { // show stats
                    crawler.showStats();
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

    private void startAnalyzer() throws IOException {
        analyzerManager = new AnalyzerManager(dataSource, analyzerThreads);
        analyzerManager.start();
        System.out.println("Analyzer Started.");
    }

    private void showStats() throws Exception {
        String stats;
        stats = statsProvider.getStats();
        System.out.println("\n" + stats);
    }

    // TODO make it threaded so that injector and fetcherTransformerManager can run in parallel
    private int inject(String fileName) throws IOException, SQLException {
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

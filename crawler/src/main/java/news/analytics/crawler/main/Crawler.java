package news.analytics.crawler.main;

import news.analytics.crawler.fetch.Fetcher;
import news.analytics.crawler.inject.Injector;
import news.analytics.crawler.stats.StatsProvider;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
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
        System.out.println("Properties initialized successfully.");
        dataSource = H2DataSource.getDataSource(properties.getProperty("driverClass"), properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"));
        System.out.println("Data source and connection pool initialized successfully.");

        injector = new Injector(dataSource);
        System.out.println("Injector initialized.");
        statsProvider = new StatsProvider(dataSource);

        fetcher = new Fetcher(dataSource, Integer.parseInt(properties.getProperty("fetcherThreads")));
        fetcher.start();
        System.out.println("Fetcher started.");

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
                if(input.equalsIgnoreCase("1")) { // inject
                    System.out.println("Enter full file path for seed url:");
                    input = sc.nextLine();
                    crawler.inject(input);
                } else if(input.equalsIgnoreCase("2")) { // fetch
                    crawler.startTransformer();
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

    private void showStats() throws Exception {
        String stats;
        stats = statsProvider.getStats();
        System.out.println("\n" + stats);
    }

    // TODO make it threaded so that injector and fetcher can run in parallel
    private int inject(String fileName) throws IOException, SQLException {
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

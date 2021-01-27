package news.analytics.crawler.utils;

import news.analytics.pipeline.fetch.FetchStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String get(String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL uri = new URL(url);
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
        return sb.toString();
    }
}

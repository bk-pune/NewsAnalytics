package news.analytics.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class SeleniumTest {
    public static void main(String[] args) {
        String exePath = "D:\\shared\\grid setup (deepa)\\LatestChrome\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", exePath);
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.esakal.com/desh/those-who-celebrated-mahatma-gandhis-assassination-are-power-today-says-swara-bhasker-141210");
        List<WebElement> html = driver.findElements(By.className("comment"));
        String innerHTML = html.get(0).getText();
        System.out.println(innerHTML);
    }
}

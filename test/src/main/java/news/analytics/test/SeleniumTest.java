package news.analytics.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class SeleniumTest {
    public static void main(String[] args) throws InterruptedException {
        String exePath = "D:\\shared\\grid setup (deepa)\\LatestChrome\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", exePath);
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.esakal.com/desh/those-who-celebrated-mahatma-gandhis-assassination-are-power-today-says-swara-bhasker-141210");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement popupBoxClose = wait.until(presenceOfElementLocated(By.id("popupBoxClose")));
        while (!popupBoxClose.isDisplayed()) {
            Thread.sleep(200);
        }
        popupBoxClose.click();
        Thread.sleep(2000);
        List<WebElement> elementsByClassName = driver.findElements(By.id("vuukle-comments"));
        for(WebElement ele : elementsByClassName) {
            System.out.println(ele.getText());
        }

        driver.close();
    }
}

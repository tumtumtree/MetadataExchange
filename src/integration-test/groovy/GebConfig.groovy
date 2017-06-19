/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

ChromeOptions options = new ChromeOptions()
options.addArguments("test-type")
options.addArguments("--disable-extensions")
ChromeDriverManager.getInstance().setup()


reportsDir = new File("build//geb-reports")
reportOnTestFailureOnly = false
//baseUrl = 'http://localhost:8080/'
//baseUrl = 'https://gel-mc-test.metadata.org.uk/'

waiting {
    timeout = 15
    retryInterval = 0.6
}

atCheckWaiting = true

environments {

    htmlUnit {
        driver = { new HtmlUnitDriver() }
    }

    chrome {
        driver = { new ChromeDriver(options) }
    }

    firefox {
        driver = { new FirefoxDriver() }
    }

    phantomJs {
        driver = { new PhantomJSDriver() }
    }
}


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.List;
import java.util.Vector;

/**
 * Created by Vladi on 28/09/15.
 */
public class Automator {

    protected WebDriver driver;
    protected List<Long> timers = new Vector<>();

    public Automator() {
        driver = new SafariDriver();
    }

    public void destroy() {
        driver.quit();
    }
}

package verify;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ObjectUtils.Null;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.sun.webkit.WebPage;

public class GoogleWebTranslator {
	static WebDriver driver = null;
	
	public static String translate(String sentence,String target) throws InterruptedException {
		if (driver==null) {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			driver = new ChromeDriver();
			driver.get("https://translate.google.cn/#ru/"+target+"/");
		}
		
		//make sure clear
		WebElement source = driver.findElement(By.id("source"));
		source.clear();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		for(boolean isClear=false;!isClear;Thread.sleep(5)){
			try {
				WebElement firstResultSpan = driver.findElement(By.xpath("//span[@id='result_box']/span"));
			} catch (Exception e) {
				// TODO: handle exception
				break;
			}
		}
		
		source = driver.findElement(By.id("source"));
		source.sendKeys(sentence);
//		WebElement submit = driver.findElement(By.id("gt-submit"));
//		submit.click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		List<WebElement> resultSpans = driver.findElements(By.xpath("//span[@id='result_box']/span"));
		String resultString = "";
		for (Iterator iterator = resultSpans.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			resultString = resultString+ webElement.getText();
		}
		return resultString;
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		driver.close();
	}
}

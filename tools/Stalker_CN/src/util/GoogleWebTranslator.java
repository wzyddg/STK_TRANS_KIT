package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GoogleWebTranslator {
	static WebDriver driver = null;
	static protected Map<String, String> langMap = new HashMap<>();
	
	private static String getResultSentence() {
		List<WebElement> resultSpans = driver.findElements(By.xpath("//span[@id='result_box']/span"));
		String resultString = "";
		for (Iterator iterator = resultSpans.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			resultString = resultString+ (webElement.getText().length()>0?webElement.getText():" ");
		}
		return resultString;
	}
	
	public static String translate(String sentence,String target) throws Exception  {
		if (driver==null) {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			driver = new ChromeDriver();
			langMap.put("en", "en");
			langMap.put("zh", "zh-CN");
			driver.get("https://translate.google.cn/#ru/"+langMap.get(target)+"/");
		}
		
		//make sure clear
		WebElement source = driver.findElement(By.id("source"));
		source.clear();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		for(boolean isClear=false;!isClear;Thread.sleep(5)){
			try {
				driver.findElement(By.xpath("//span[@id='result_box']/span"));
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
		
		return getResultSentence();
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		driver.close();
	}
}

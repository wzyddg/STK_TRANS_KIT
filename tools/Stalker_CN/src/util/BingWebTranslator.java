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

public class BingWebTranslator {
	static WebDriver driver = null;
	static protected Map<String, String> langMap = new HashMap<>();
	static String lastResult = "";
	
	private static String getResultSentence() {
		List<WebElement> resultSpans = driver.findElements(By.xpath("//div[@id='destText']/div/span"));
		String resultString = "";
		for (Iterator iterator = resultSpans.iterator(); iterator.hasNext();) {
			WebElement webElement = (WebElement) iterator.next();
			resultString = resultString+ (webElement.getText().length()>0?webElement.getText():" ");
		}
		return resultString;
	}
	
	public static String translate(String sentence,String target) throws Exception {
		if (driver==null) {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
			driver = new ChromeDriver();
			driver.get("http://cn.bing.com/translator/");
			langMap.put("en", "en");
			langMap.put("zh", "zh-CHS");
//			Thread.sleep(2000);
			WebElement lang = driver.findElements(By.id("LS_LangList")).get(1);
			lang.click();
//			Thread.sleep(3000);
			WebElement targetLang = driver.findElements(By.xpath("//*[@id='LS_LangList']/table/tbody/tr/td[@value='"+langMap.get(target)+"']")).get(1);
			targetLang.click();
		}
		
		long startTime = System.currentTimeMillis();
		//make sure clear
		WebElement source = driver.findElement(By.id("srcText"));
		source.clear();
		source = driver.findElement(By.id("srcText"));
		source.sendKeys(sentence);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		for(boolean isClear=false;!isClear;){
			String now = "";
			try {
				now = getResultSentence();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (lastResult.equals(now)) {
				if (System.currentTimeMillis()-startTime>6000) {
					throw new Exception("waste too long, please retry.");
				}else {
					Thread.sleep(10);
				}
			}else {
				break;
			}
		}
		lastResult = getResultSentence();
		return lastResult;
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		driver.close();
	}
}

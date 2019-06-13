package com.cb.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainScript {
	
	WebDriver driver;
	WebDriverWait wait;
	File pricesFile;
	FileWriter writer;
	
	String userid =  "KQ2863";
	String password =  "RUHULKHAN07866";
	String pin = "078666";
	
	public MainScript(){
		System.setProperty("webdriver.chrome.driver", "./driver/chromedriver_2.40.exe");
		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless");
		options.addArguments("--start-maximized");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, 15);
	}
	public void startProcess(){
		System.out.println("Connecting to Kite Zerodha ... ");
		driver.get("https://kite.zerodha.com");
		goodNight(2);
		System.out.println("Logging in to account ... ");
		System.out.println("Entering User ID");
		getElement("//input[@placeholder='User ID']").sendKeys(userid);
		System.out.println("Entering password");
		getElement("//input[@placeholder='Password']").sendKeys(password);
		getElement("//button[@type='submit']").click();
		System.out.println("Entering PIN ");
		getElement("//input[@type='password' and @placeholder='PIN']").sendKeys(pin);
		System.out.println("Continue with PIN");
		getElement("//button[@type='submit']").click();
		System.out.println("Navigating to Positions tab");
		getElement("//span[text()='Positions']/parent::a[contains(@href,'positions')]").click();
		System.out.println("Fetching prices ... ");
		
		String spotPrice="";
		String callPrice="";
		String putPrice="";
		
		pricesFile = createPriceFile();
		if(pricesFile==null){
			System.out.println("Prices file not created. Aborting the program");
			System.exit(0);
		}
		try {
			writer = new FileWriter(pricesFile);
		} catch (IOException e) {
			System.out.println("Couldn't create writer to prices file. Aborting the program!");
			e.printStackTrace();
			System.exit(0);
		}
		
		int index=1;
		while(true){
			System.out.println("\n+++++++++++++++++++++++++++++++");
			spotPrice = getElement("(//div[@class='info']//span[@class='last-price'])[3]").getText().trim();
			callPrice = getElement("(//div[@class='info']//span[@class='last-price'])[4]").getText().trim();
			putPrice = getElement("(//div[@class='info']//span[@class='last-price'])[5]").getText().trim();
			System.out.println("Spot Price : "+spotPrice);
			System.out.println("Call Price : "+callPrice);
			System.out.println("Put Price : "+putPrice);
			System.out.println("++++++++++++++++++++++++++++++++");
			index++;
			updateDataInFile(spotPrice+"_"+index, callPrice, putPrice);
			goodNight(1);
		}
	}
	
	private void updateDataInFile(String spot, String call, String put){
		String content = spot+","+call+","+put;
		try {
			Files.write(Paths.get("./prices.txt"),content.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void goodNight(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private WebElement getElement(String xpath){
		WebElement element = null;
		try{
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath(xpath)));
		}catch(Exception e){
			System.out.println("There was some problem finding element on page. "+"\n"+xpath);
			e.printStackTrace();
		}
		System.out.println("WebElement is : "+element);
		return element;
	}
	
	private File createPriceFile(){
		File file = new File("./prices.txt");
		if(file.exists())
			return file;
		else{
			boolean isFileCreated=false;
			try{
				isFileCreated = file.createNewFile();
			}catch(Exception e){
				System.out.println("Couldn't create prices.txt file. Aborting the program!");
				System.exit(0);
			}
			if(!isFileCreated){
				System.out.println("Couldn't create prices.txt file. Aborting the program!");
				System.exit(0);
			}
			return file;
		}
	}
	
}

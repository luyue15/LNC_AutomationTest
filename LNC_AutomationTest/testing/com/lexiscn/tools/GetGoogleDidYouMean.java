package com.lexiscn.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Content team给了一些日常用的词语
 * 这个程序是从Google里面抓取这些日常词语的did you mean
 * @author jiangkx
 *
 */
public class GetGoogleDidYouMean {

	public static void main(String[] args) {
		GetGoogleDidYouMean ggdym = new GetGoogleDidYouMean();
		String[] verbals = ggdym.getVerbalString();
		for (String verbal: verbals) {
			System.out.println(verbal + " | " + StringUtils.join(ggdym.getGoogleWords(verbal), " "));
		}
		ggdym.quiteDriver();
	}
	
	String outputFilename = "C:/project/Oct Release 2013/google_did_you_mean_tmp.txt";
	WebDriver driver;
	WebDriverWait wait;
	BufferedWriter br;
	
	public GetGoogleDidYouMean() {
		driver = new FirefoxDriver();
		wait = new WebDriverWait(driver, 10);
		try {
			br = new BufferedWriter(new FileWriter(outputFilename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void quiteDriver() {
		driver.close();
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String[] getGoogleWords(String keysToSend) {
		driver.get("https://www.google.com.hk/webhp?hl=zh-CN");
		WebElement inputBox = driver.findElement(By.id("lst-ib"));
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(keysToSend+Keys.ENTER);
		String[] suggestions = null;
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.cssSelector("#rso")));

			List<WebElement> sgst = driver.findElements(By.cssSelector("#brs .brs_col p a"));
			suggestions = new String[sgst.size()];
			for (int i=0; i<sgst.size(); i++) {
				suggestions[i] = sgst.get(i).getText().trim();
			}
			write(keysToSend + " | " + StringUtils.join(suggestions, " , ") + "\n");
		} catch (TimeoutException | NoSuchElementException e) {
			write(keysToSend + " | \n");
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return suggestions;
	}
	
	protected void write(String line) {
		try {
			br.write(line);
			br.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public String[] getVerbalString() {
		String excelFilename = "C:/project/Oct Release 2013/法律术语(20130716).xlsx";
		String[] ret = null;
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(OPCPackage.open(excelFilename));
			XSSFRow row;
			
			int sheetNum = xwb.getNumberOfSheets();
			for (int i=0; i<sheetNum; i++) {
				XSSFSheet sheet = xwb.getSheetAt(i);
				int rowNum = sheet.getPhysicalNumberOfRows();
				ret = new String[rowNum];
				
				// 循环每一个sheet的每一行
				for (int j=sheet.getFirstRowNum(); j<sheet.getPhysicalNumberOfRows(); j++) {
					row = sheet.getRow(j);
					ret[j] = row.getCell(0).toString();
				}
				if (i == 1) {
					break;
				}
			}
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}

}

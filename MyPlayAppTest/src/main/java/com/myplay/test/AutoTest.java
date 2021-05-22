package com.myplay.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoTest {

	final static String baseURL = "http://localhost:8080";
	String recomTrack;
	String recomArtist;
	WebDriver driver;

	@BeforeTest
	public void createDriver() {
		if (System.getProperty("webdriver.chrome.driver") == null)
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\reddy\\lib\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	@AfterTest
	public void destroyDriver() {
		driver.close();
	}

	@Test(priority = 1, description = "Searching without login using track")
	public void searchWithoutLoginTrack() throws InterruptedException {
		driver.get(baseURL);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("Wolves");
		driver.findElement(By.id("search")).click();
		Thread.sleep(6000);
		assertEquals(true, driver.getPageSource().contains("Search Results"));

	}

	@Test(priority = 2, description = "Searching without login using artists")
	public void searchWithoutLoginArtist() throws InterruptedException {
		driver.get(baseURL);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("artist");
		driver.findElement(By.id("query")).sendKeys("DJ Snake");
		driver.findElement(By.id("search")).click();
		Thread.sleep(6000);
		assertEquals(true, driver.getPageSource().contains("Search Results"));

	}
	
	@Test(priority = 3, description = "Searching without login using artists and add to favourites")
	public void searchWithoutLoginByArtistAddFavourite() throws InterruptedException {
		driver.get(baseURL);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("artist");
		driver.findElement(By.id("query")).sendKeys("DJ Snake");
		driver.findElement(By.id("search")).click();
		Thread.sleep(6000);
		driver.findElement(By.xpath("//*[@id=\"fav_btn_1\"]")).click();
		Thread.sleep(1000);
		String alert = driver.switchTo().alert().getText();
		driver.switchTo().alert().accept();
		Thread.sleep(3000);
		assertTrue(alert.equals("Please login to save recommendations / favourites"));

	}

	@Test(priority = 4, description = "Validating User SignUp button")
	public void validatingSignUpbutton() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[1]/a"));
		signUpPage.click();
		Thread.sleep(3000);
		assertEquals(true, driver.getPageSource().contains("MyPlayApp Registration"));
	}

	@Test(priority = 5, description = "Validating Home button from Registration page")
	public void validatingHomebuttonFromRegisterPage() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[1]/a"));
		signUpPage.click();
		driver.findElement(By.xpath("//*[@id=\"login-box\"]/div/a")).click();
		Thread.sleep(3000);
		assertEquals("MyPlayApp", driver.getTitle());
	}

	@Test(priority = 6, description = "Validating Home button from Login page")
	public void validatingHomebuttonFromLoginPage() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[2]/a"));
		signUpPage.click();
		driver.findElement(By.xpath("//*[@id=\"login-form\"]/div[3]/a")).click();
		Thread.sleep(3000);
		assertEquals("MyPlayApp", driver.getTitle());
	}
	@Test(priority = 7, description = "Validating User Registration Form")
	public void uiFormValidation() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[1]/a"));
		signUpPage.click();
		Thread.sleep(3000);
		driver.findElement(By.id("name")).sendKeys("AUTO USER");
		driver.findElement(By.id("email")).sendKeys("auto@test.com");
		driver.findElement(By.id("password")).sendKeys("password");
		driver.findElement(By.id("confpassword")).sendKeys("wrongpassword");
		driver.findElement(By.id("signup")).click();
		Thread.sleep(2000);
		String errorText = driver.findElement(By.id("error_msg")).getText();
		assertEquals(true, errorText.contains("Password and confirm password must match"));
	}

	@Test(priority = 8, description = "Validating User Registration Form with Username field blank")
	public void uiFormValidationUsername() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[1]/a"));
		signUpPage.click();
		Thread.sleep(3000);
		driver.findElement(By.id("signup")).click();
		Thread.sleep(2000);
		String errorText = driver.findElement(By.id("error_msg")).getText();
		assertEquals(true, errorText.contains("Please enter user name"));
	}

	@Test(priority = 9, description = "Validating User Registration Form with invalid email")
	public void uiFormValidationEmail() throws InterruptedException {
		driver.get(baseURL);
		WebElement signUpPage;
		signUpPage = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/li[1]/a"));
		signUpPage.click();
		Thread.sleep(3000);
		driver.findElement(By.id("name")).sendKeys("AUTO USER");
		driver.findElement(By.id("password")).sendKeys("password");
		driver.findElement(By.id("confpassword")).sendKeys("password");
		driver.findElement(By.id("signup")).click();
		Thread.sleep(2000);
		String errorText = driver.findElement(By.id("error_msg")).getText();
		assertEquals(true, errorText.contains("Please enter valid email"));
	}
	
	@Test(priority = 10, description = "User login validation")
	public void loginUserFail() throws InterruptedException {
		driver.get(baseURL + "/login");
		Thread.sleep(2000);
		driver.findElement(By.id("username")).sendKeys("rajashekhar@gmail.com");
		driver.findElement(By.id("password")).sendKeys("12345");
		driver.findElement(By.id("login_btn")).click();
		Thread.sleep(2000);
		driver.get(baseURL);
		assertEquals(false, driver.getPageSource().contains("rajashekhar"));
	}
	
	@Test(priority = 11, description = "User login and check user name displayed in header")
	public void loginUser() throws InterruptedException {
		driver.get(baseURL + "/login");
		Thread.sleep(2000);
		driver.findElement(By.id("username")).sendKeys("rajashekhar@gmail.com");
		driver.findElement(By.id("password")).sendKeys("123456789");
		driver.findElement(By.id("login_btn")).click();
		Thread.sleep(2000);
		driver.get(baseURL);
		assertEquals(true, driver.getPageSource().contains("rajashekhar"));
	}

	@Test(priority = 12, description = "Search tracks add track into recommendations")
	public void searchTrack() throws InterruptedException {

		Thread.sleep(2000);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("arjith");
		driver.findElement(By.id("search")).click();
		Thread.sleep(30000);

		recomTrack = driver.findElement(By.id("track_3")).getText();
		driver.findElement(By.xpath("//*[@id=\"rec_btn_3\"]")).click();
		Thread.sleep(30000);
		assertEquals(true, true);
	}

	@Test(priority = 13, description = "Validating tracks added into recommendations")
	public void verifyRecommendation() throws InterruptedException {

		driver.get(baseURL + "/MyMusic");
		Thread.sleep(2000);

		assertEquals(true, driver.getPageSource().contains(recomTrack));

	}

	@Test(priority = 14, description = "Deleting track from recommendations")
	public void deleteRecommendation() throws InterruptedException {
		recomArtist = driver.findElement(By.id("artist_1")).getText();

		List<WebElement> buttons = driver.findElements(By.tagName("button"));
		for (int i = 0; i < buttons.size(); i++) {
			if ((buttons.get(i).getAttribute("onclick")) != null
					&& buttons.get(i).getAttribute("onclick").contains("deleteItem")) {
				buttons.get(i).click();
				break;
			}
		}

		Thread.sleep(5000);
		assertEquals(false, driver.getPageSource().contains(recomArtist));

	}

	@Test(priority = 15, description = "Validating page Number after searching")
	public void searchTrackAndValidatePageNumber() throws InterruptedException {
		driver.get(baseURL);
		Thread.sleep(2000);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("wolves");
		driver.findElement(By.id("search")).click();
		Thread.sleep(4000);
		assertEquals(true, driver.getPageSource().contains("Page Number"));

	}

	@Test(priority = 16, description = "Validating items per page after searching")
	public void searchTrackAndValidateItemsPerPage() throws InterruptedException {
		driver.get(baseURL);
		Thread.sleep(2000);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("wolves");
		driver.findElement(By.id("search")).click();
		Thread.sleep(4000);
		assertEquals(true, driver.getPageSource().contains("Items Per Page"));

	}

	@Test(priority = 17, description = "Validating page Number after searching")
	public void searchTrackAndValidateNextPage() throws InterruptedException {
		driver.get(baseURL);
		Thread.sleep(2000);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("wolves");
		driver.findElement(By.id("search")).click();
		Thread.sleep(4000);
		driver.findElement(By.id("page_number")).click();
		driver.findElement(By.xpath("//*[@id=\"page_number\"]/option[3]")).click();
		String songName = driver.findElement(By.id("track_1")).getText();
		assertEquals(true, songName.contains("Wolves in Wolves' Clothing"));

	}

	@Test(priority = 18, description = "Validating number of items per page after searching")
	public void searchTrackAndValidateNumberOfItemPerPage() throws InterruptedException {
		driver.get(baseURL);
		Thread.sleep(2000);
		Select filter = new Select(driver.findElement(By.id("filter")));
		filter.selectByValue("track");
		driver.findElement(By.id("query")).sendKeys("wolves");
		driver.findElement(By.id("search")).click();
		Thread.sleep(4000);
		Select select = new Select(driver.findElement(By.id("items")));
		select.selectByValue("500");
		Thread.sleep(5000);
		String song = driver.findElement(By.id("artist_500")).getText();
		assertEquals(true, song.contains("Hearts Of Black Science"));

	}
	
	@Test(priority = 19, description = "Verifing user log out")
	public void verifyLogOut() throws InterruptedException {

		driver.get(baseURL + "/logout");
		Thread.sleep(2000);
		driver.get(baseURL);
		Thread.sleep(5000);
		assertEquals(false, driver.getPageSource().contains("rajashekhar"));

	}
	
	@Test(priority = 20, description = "User login and check user name displayed in header")
	public void loginUserAfterLogout() throws InterruptedException {
		driver.get(baseURL + "/login");
		Thread.sleep(2000);
		driver.findElement(By.id("username")).sendKeys("raja@gmail.com");
		driver.findElement(By.id("password")).sendKeys("654321");
		driver.findElement(By.id("login_btn")).click();
		Thread.sleep(2000);
		driver.get(baseURL);
		assertEquals(true, driver.getPageSource().contains("raja"));
	}
	
	@Test(priority = 21, description = "Validating the profile")
	public void verfiyProfile() throws InterruptedException {
		
		WebElement profile = driver.findElement(By.xpath("//*[@id=\"checked_login\"]/ul/a"));
		profile.click();
		assertEquals(true, driver.getCurrentUrl().contains("profile"));
		assertEquals("MyPlayApp | Profile", driver.getTitle());
	}
	
	@Test(priority = 22, description = "Validating Update Password")
	public void validateUpdatePassword() throws InterruptedException {

		driver.get(baseURL + "/profile");
		Thread.sleep(2000);
		assertEquals(true, driver.getPageSource().contains("raja"));
		driver.findElement(By.id("oldpass")).sendKeys("654321");
		driver.findElement(By.id("newpass")).sendKeys("123456");
		driver.findElement(By.name("submit")).click();
		Thread.sleep(5000);
		String errorText = driver.findElement(By.id("error_msg")).getText();
		assertEquals(true, errorText.contains("Profile updated"));

	}

	@Test(priority = 23, description = "Verifing user log out")
	public void validateLogOut() throws InterruptedException {

		driver.get(baseURL + "/logout");
		Thread.sleep(2000);
		driver.get(baseURL);
		Thread.sleep(5000);
		assertEquals(false, driver.getPageSource().contains("raja"));

	}
	
	@Test(priority = 24, description = "Verifing user login after Updating password")
	public void verifyLoginAfterUpdatingPassword() throws InterruptedException {

		driver.get(baseURL + "/login");
		Thread.sleep(2000);
		driver.findElement(By.id("username")).sendKeys("raja@gmail.com");
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("login_btn")).click();
		Thread.sleep(2000);
		driver.get(baseURL);
		assertEquals(true, driver.getPageSource().contains("raja"));
	}
	
	@Test(priority = 25, description = "Verifing user log out after updating password")
	public void verifyLogOutAterUpdatingPassword() throws InterruptedException {

		driver.get(baseURL + "/logout");
		Thread.sleep(2000);
		driver.get(baseURL);
		Thread.sleep(5000);
		assertEquals(false, driver.getPageSource().contains("raja"));

	}
}

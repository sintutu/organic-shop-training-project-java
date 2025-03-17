package com.sintutu.organicshopuitests.utils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.pages.BreadPage;
import com.sintutu.organicshopuitests.pages.DairyPage;
import com.sintutu.organicshopuitests.pages.HomePage;
import com.sintutu.organicshopuitests.pages.LoginPage;
import com.sintutu.organicshopuitests.pages.ShoppingCartPage;

public abstract class TestBase {
    private WebDriver driver;
    private WebDriverWait wait;
    private PageFactory pageFactory;

    // Common Locators
    private static final By SHOPPING_CART_LINK = By.partialLinkText("Shopping Cart");
    private static final By LOGIN_LINK = By.linkText("Login");

    // Declare PageObjects
    protected HomePage homePage;
    protected BreadPage breadPage;
    protected DairyPage dairyPage;
    protected ShoppingCartPage shoppingCartPage;
    protected LoginPage loginPage;

    //Create the WebDriver
    @BeforeEach
    private void setUp() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("drivers/chromedriver.exe");
        if (resource != null) {
            System.setProperty("webdriver.chrome.driver", Paths.get(resource.toURI()).toString());
        }
        driver = new ChromeDriver();

        // Create WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Initialize PageFactory
        pageFactory = new PageFactory(driver, wait);

        // Initialize PageObjects
        homePage = pageFactory.getHomePage();
        breadPage = pageFactory.getBreadPage();
        dairyPage = pageFactory.getDairyPage();
        shoppingCartPage = pageFactory.getShoppingCartPage();
        loginPage = pageFactory.getLoginPage();
    }

    protected void clickShoppingCartLink(){
        WebElement shoppingCartLink = wait.until(ExpectedConditions.elementToBeClickable(SHOPPING_CART_LINK));
        shoppingCartLink.click();
    }

    protected void clickLoginLink(){
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_LINK));
        loginLink.click();
    }

    @AfterEach
    private void tearDown(){
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

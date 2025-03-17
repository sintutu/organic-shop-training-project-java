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

public class TestBase {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected PageFactory pageFactory;

    // Common Locators
    public static final By SHOPPING_CART_LINK = By.partialLinkText("Shopping Cart");
    public static final By LOGIN_LINK = By.linkText("Login");

    //Create the WebDriver
    @BeforeEach
    public void setUp() throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("drivers/chromedriver.exe");
        if (resource != null) {
            System.setProperty("webdriver.chrome.driver", Paths.get(resource.toURI()).toString());
        }
        driver = new ChromeDriver();

        // Create WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Initialize PageFactory
        pageFactory = new PageFactory(driver, wait);
    }

    protected void goToShoppingCart(){
        WebElement shoppingCartLink = wait.until(ExpectedConditions.elementToBeClickable(SHOPPING_CART_LINK));
        shoppingCartLink.click();
    }

    protected void goToLoginPage(){
        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_LINK));
        loginLink.click();
    }

    @AfterEach
    public void tearDown(){
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

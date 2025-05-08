package com.sintutu.organicshopuitests.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.utils.ConfigReader;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    // Locators
    private static final By LOGIN_WITH_GOOGLE_BUTTON =  By.xpath("//button[normalize-space(text()) = 'Login with Google']");

    // Constructor
    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;      
    }

    // Assert page is loaded
    public void checkLoginPageIsLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_WITH_GOOGLE_BUTTON));  
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri()+"login", url, "Login Page did not load as expected from Login link.");
    }

    public void checkLoginPageIsLoadedViaCheckout() {
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_WITH_GOOGLE_BUTTON));  
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri()+"login?returnUrl=check-out", url, "Login Page did not load as expected from Check Out link.");
    }
}

package com.sintutu.organicshopuitests.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.utils.ConfigReader;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;    
    // Locators
    private static final By BREAD_LINK = By.linkText("Bread");
    private static final By DAIRY_LINK = By.linkText("Dairy");

    // Constructor
    public HomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait; // Initialize WebDriverWait
    }

    // Load the Home Page
    public void load() {
        driver.get(ConfigReader.getBaseUri());
    }

    // Assert page is loaded
    public void assertHomePageIsLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(BREAD_LINK)); // Ensures DOM contains the element
        wait.until(ExpectedConditions.presenceOfElementLocated(DAIRY_LINK)); // Ensures DOM contains the element
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri(), url, "Home Page did not load as expected.");
    }

    public void clickBreadLink() {
        WebElement breadLink = wait.until(ExpectedConditions.elementToBeClickable(BREAD_LINK));
        breadLink.click();
    }

    public void clickDairyLink() {
        WebElement dairyLink = wait.until(ExpectedConditions.elementToBeClickable(DAIRY_LINK));
        dairyLink.click();
    }
}

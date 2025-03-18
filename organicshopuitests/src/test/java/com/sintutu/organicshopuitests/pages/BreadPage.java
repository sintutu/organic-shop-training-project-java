package com.sintutu.organicshopuitests.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.utils.ConfigReader;

public class BreadPage { 
    private WebDriver driver;
    private WebDriverWait wait;
    // Locators
    private static final By ADD_FRENCH_BAGUETTE_TO_CART = By.xpath("//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]");

    // Constructor
    public BreadPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;      
    }

    // Assert page is loaded
    public void checkBreadCategoryIsLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(ADD_FRENCH_BAGUETTE_TO_CART));  
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri()+"?category=bread", url, "Bread Category did not load as expected.");
    }

    public void addFrenchBaguetteToCart() {
        WebElement addFrenchBaguetteToCartButton = wait.until(ExpectedConditions.elementToBeClickable(ADD_FRENCH_BAGUETTE_TO_CART));  
        addFrenchBaguetteToCartButton.click();
    }
}

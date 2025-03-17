package com.sintutu.organicshopuitests.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.utils.ConfigReader;

public class DairyPage {
    private WebDriver driver;
    private WebDriverWait wait;
    // Locators
    private static final By ADD_CHEESE_TO_CART = By.xpath("//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), 'Add to Cart')]");
    private static final By INCREMENT_CHEESE_IN_CART = By.xpath("//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), '+')]");

    // Constructor
    public DairyPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait; // Initialize WebDriverWait       
    }

    // Assert page is loaded
    public void assertDairyCategoryIsLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(ADD_CHEESE_TO_CART)); 
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri()+"?category=dairy", url, "Dairy Category did not load as expected.");
    }

    public void addCheeseToCart() {
        WebElement addCheeseToCartButton = wait.until(ExpectedConditions.elementToBeClickable(ADD_CHEESE_TO_CART));
        addCheeseToCartButton.click();
    }

    public void incrementCheeseInCart(){
        WebElement cheesePlusButton = wait.until(ExpectedConditions.elementToBeClickable(INCREMENT_CHEESE_IN_CART));
        cheesePlusButton.click();
    }
}

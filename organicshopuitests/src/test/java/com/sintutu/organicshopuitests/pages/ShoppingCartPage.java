package com.sintutu.organicshopuitests.pages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.utils.ConfigReader;

public class ShoppingCartPage {
    private WebDriver driver;
    private WebDriverWait wait;
    // Locators
    private static final By CHEESE_MINUS_BUTTON =  By.xpath("//table//tr[td[normalize-space(text()) = 'Cheese']]//button[normalize-space(text()) = '-']");
    private static final By CHECK_OUT_LINK = By.linkText("Check Out");

    // Constructor
    public ShoppingCartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;      
    }

    // Assert page is loaded
    public void checkShopppingCartPageIsLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(CHEESE_MINUS_BUTTON));  
        String url = driver.getCurrentUrl();
        assertEquals(ConfigReader.getBaseUri()+"shopping-cart", url, "Shopping Cart page did not load as expected.");
    }

    public void removeOneCheeseFromCart() {
        WebElement cheeseMinusButton = wait.until(ExpectedConditions.elementToBeClickable(CHEESE_MINUS_BUTTON));
        cheeseMinusButton.click();
    }

    public void goToCheckOut(){
        WebElement checkOutLink = wait.until(ExpectedConditions.elementToBeClickable(CHECK_OUT_LINK));
        checkOutLink.click();
    }
}

package com.sintutu.organicshopuitests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * ## What to test

Add an end to end test performing the following:
 
1. Select the Bread Category
2. Assert that the URL has changed
3. Add a French Baguette
4. Select the Dairy Category
5. Assert that the URL has changed
6. Add 2 blocks of cheese
7. Open the cart
8. Assert that the URL has changed
9. Remove one block of cheese
10. Click checkout
11. Click Login
 */
public class EndToEndTests {
    @Test
    public void endToEndTestShouldSucceed() {
        //Create the WebDriver 
        WebDriver driver = new ChromeDriver();

        //Navigate to home page
        driver.get("https://agular-test-shop-cb70d.firebaseapp.com");
        String currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/", currentUrl);

        //1. Select the Bread Category
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Bread")));
        driver.findElement(By.linkText("Bread"));
        
        //Drop the driver
        driver.quit();
    }
}

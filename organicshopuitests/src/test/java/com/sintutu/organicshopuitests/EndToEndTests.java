package com.sintutu.organicshopuitests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    public void endToEndTestShouldSucceed() throws URISyntaxException {
        // Set the path to chromedriver
        URL resource = getClass().getClassLoader().getResource("drivers/chromedriver.exe");
        if (resource != null) {
            System.setProperty("webdriver.chrome.driver", Paths.get(resource.toURI()).toString());
        }
        //Create the WebDriver 
        WebDriver driver = new ChromeDriver();

        //Navigate to home page
        driver.get("https://agular-test-shop-cb70d.firebaseapp.com");
        String currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/", currentUrl);

        //1. Select the Bread Category
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement breadLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Bread")));
        breadLink.click();

        //2. Assert that the URL has changed
        currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/?category=bread", currentUrl);

        //3. Add a French Baguette
        /*
         * <product-card _ngcontent-c2="" _nghost-c4="">
         *   <!---->
         *   <div _ngcontent-c4="" class="card">
         *     <!---->
         *     <img _ngcontent-c4="" class="card-img-top" layout-fill="" src="https://upload.wikimedia.org/wikipedia/commons/f/f5/Baguettes_-_stonesoup.jpg" alt="Fresh French Baguette">
         *     <div _ngcontent-c4="" class="card-body">
         *       <h5 _ngcontent-c4="" class="card-title">Fresh French Baguette</h5>
         *       <p _ngcontent-c4="" class="card-text">$3.00</p>
         *     </div>
         *       <!---->
         *     <div _ngcontent-c4="" class="card-footer">
         *       <!---->
         *       <button _ngcontent-c4="" class="btn btn-secondary btn-block">Add to Cart </button>
         *       <!---->
         *     </div>
         *   </div>
         * </product-card>
         */
        WebElement addToCartFreshFrenchBaguette 
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]\r\n"
                )));
        addToCartFreshFrenchBaguette.click();

        // 4. Select the Dairy Category
        WebElement dairyLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Dairy")));
        dairyLink.click();
        
        // 5. Assert that the URL has changed
        currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/?category=dairy", currentUrl);

        // 6. Add 2 blocks of cheese
        WebElement addToCartCheese 
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), 'Add to Cart')]\r\n"
                )));
        addToCartCheese.click();
        WebElement addOneMoreToCartCheese
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), '+')]\r\n"
                )));
        addOneMoreToCartCheese.click();

        // 7. Open the cart
        WebElement shoppingCartLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Shopping Cart")));
        shoppingCartLink.click();

        // 8. Assert that the URL has changed
        currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/shopping-cart", currentUrl);        

        // 9. Remove one block of cheese
        // <table _ngcontent-c6="" class="table">
        //   <thead _ngcontent-c6="">
        //     <tr _ngcontent-c6="">
        //       <th _ngcontent-c6=""></th>
        //       <th _ngcontent-c6="">Product</th>
        //       <th _ngcontent-c6="" class="text-center" style="width: 230px">Quantity</th>
        //       <th _ngcontent-c6="" class="text-right" style="width: 200px">Price</th>
        //     </tr>
        //   </thead>
        //   <tbody _ngcontent-c6=""><!---->
        //     <tr _ngcontent-c6="">
        //       <td _ngcontent-c6="">
        //       <img _ngcontent-c6="" alt="No Image" class="thumbnail" src="https://upload.wikimedia.org/wikipedia/commons/2/25/Maasdam-cheese.jpg"></td>
        //       <td _ngcontent-c6=""> Cheese </td>
        //       <td _ngcontent-c6="">
        //         <product-quantity _ngcontent-c6="" _nghost-c5="">
        //           <div _ngcontent-c5="" class="row no-gutters">
        //             <div _ngcontent-c5="" class="col-2">
        //               <button _ngcontent-c5="" class="btn btn-secondary btn-block">-</button>
        //             </div>
        //             <div _ngcontent-c5="" class="col text-center"> 2 in cart </div>
        //             <div _ngcontent-c5="" class="col-2">
        //               <button _ngcontent-c5="" class="btn btn-secondary btn-block">+</button>
        //             </div>
        //           </div>
        //         </product-quantity>
        //       </td>
        //       <td _ngcontent-c6="" class="text-right"> $24.00 </td>
        //     </tr>
        //   </tbody>
        //   <tfoot _ngcontent-c6="">
        //     <tr _ngcontent-c6="">
        //       <th _ngcontent-c6=""></th>
        //       <th _ngcontent-c6=""></th>
        //       <th _ngcontent-c6=""></th>
        //       <th _ngcontent-c6="" class="text-right"> $24.00 </th>
        //     </tr>
        //   </tfoot>
        // </table>
        WebElement removeOneMoreFromCartCheese
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//table[.//td[contains(text(), 'Cheese')]]//button[contains(text(), '-')]\r\n"
                )));
        removeOneMoreFromCartCheese.click();

        // 10. Click checkout
        WebElement checkOutLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Check Out")));
        checkOutLink.click();

        // 11. Click Login
        WebElement loginLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Login")));
        loginLink.click();

        //Drop the driver
        driver.quit();
    }
}

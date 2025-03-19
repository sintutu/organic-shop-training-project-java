# Page Object Model Learnings

Why use the page object model? Because it makes the tests look pretty. 

Where it started:
```java
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
        WebElement addToCartFreshFrenchBaguette 
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]"
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
                "//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), 'Add to Cart')]"
                )));
        addToCartCheese.click();

        WebElement addOneMoreToCartCheese
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
                "//product-card[.//h5[contains(text(), 'Cheese')]]//button[contains(text(), '+')]"
                )));
        addOneMoreToCartCheese.click();

        // 7. Open the cart
        WebElement shoppingCartLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Shopping Cart")));
        shoppingCartLink.click();

        // 8. Assert that the URL has changed
        currentUrl = driver.getCurrentUrl();
        assertEquals("https://agular-test-shop-cb70d.firebaseapp.com/shopping-cart", currentUrl);        

        // 9. Remove one block of cheese
        WebElement removeOneMoreFromCartCheese
        = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(
"//table//tr[td[normalize-space(text()) = 'Cheese']]//button[normalize-space(text()) = '-']"                )));
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
```

Where it is now:

```java
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
```

EndToEndTests.java looks so elegant. 

## Tests focus on user journey

It reads like someone is opening up Google Chrome and going shopping.

```mermaid
journey
    title Go Shopping
      Go to home page :5: customer
      Go to bread category :5: customer
      Add a baguette :5: customer
      Go to dairy category :5: customer
      Get 2 cheeses :5: customer
      Go to cart :5: customer
      Remove 1 cheese :5: customer
      Go to checkout :5: customer
      Go to login :5: customer
```

These tests say pretty much what's in that journey. **These tests don't show how this journey is implemented.** 

The home page is abstracted into a `homepage` object. The type isn't visible in the tests. The test does reveal the ability to `load()`, check I'm on the right home page with the `assertHomePageIsLoaded()`, go to the bread category with `clickBreadLink()` and the dairy category with `clickDairyLink()`. It mimics how a human being would interact with the browser to go shopping. This abstraction allows focusing on the actions.

How `homePage` even appears comes from `EndToEndTests` inheriting from `TestBase`.

<div class="design-decision">
<h5>Violating Open-Closed Principle is a design decision</h5>
<p>
Keeping implementation out of the tests means, if there were more tests then these should sit in `EndToEndTests`. If these tests are on new pages, this means `TestBase` must be modified. `TestBase` is by design not closed to modification.
</p>
</div>
<br/>

---

## What `TestBase` does

### Information Hiding

Every object and method with the `protected` access modifier is one `EndToEndTests` uses. When in `EndToEndTests` the intellisense or LSP won't pick up the `private` stuff.

```java
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
```

### `TestBase` is responsible for too much stuff
`TestBase` is responsible for:
* Providing the methods and objects the tests use.
* Environment setup e.g. reading where the driver is and loading it.

This is a violation of the Single Responsibility Principle. 

<div class="recommendation-box">
Separate these responsibilities.The driver setup is complicated and can be implemented in a different class, say <code>DriverFactory</code>. Page objects are already handled well enough by the <code>PageFactory</code>.
</div><br/>

### `TestBase` is tightly coupled to Selenium's `WebDriver` class

There will come a time to use different class. Or a different browser's driver. Woudn't it be nice to inject a dependency rather than have Selenium's `WebDriver` tightly coupled to `TestBase`? Yes. 

Another tight coupling is how the page objects use this driver because `PageFactory` uses this driver. Each page object expects precisely Selenium's `WebDriver`. For example:
```java
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
    ///code goes on...
```


The Dependency Inversion Principle suggests to abstract the particular class of driver used by using an interface. 

<div class="recommendation-box">
<h5>Use a <code>Driver</code> interface</h5>
Create a <code>public interface Driver</code> then substitute <code>public WebDriver driver</code> for <code>public Driver driver</code>.
</div><br/>

### Holds common locators

Login appears on every page the test sees. So it's common at this time. `TestBase` is where it makes the most sense to put it for now. It would be overengineering to create a separate object or file. I'll come back to this after looking at other parts.

### Revisiting `TestBase`'s responsibilities

1. `TestBase` needs to provide the pages and actions on the pages to `EndToEndTests`.
    1. The way is to make identifiers for pages and actions and use the `protected` access modifier to make these usable in `EndToEndTests`.
    2. The implementation to make these pages and actions available is hidden from `EndToEndTests`.
2. `TestBase` creates objects necessary to provide the pages and actions available. The pages are instances of page objects. The actions are methods defined inside `TestBase` or objects created inside `TestBase`. 
    1. The `WebDriver`, `WebDriverWait` and `PageFactory` are declared as private variables `driver`, `wait` and `pageFactory` at the very top of the class.
    2. Creating the objects is done by instantiating them with the `setup()` method. 
        1. The `WebDriver`, `WebDriverWait` and `PageFactory` instances are instantiated in the `setUp` method. 
        2.  There's this cascade where `wait` needs `driver` to be instantiated, and `pageFactory` needs `driver` and `wait` to be instantiated.
        3. I unwittingly tightly coupled `pageFactory` to a `WebDriver` when I created the class.
    3. The page objects `homePage`, `breadPage`, `dairyPage`, `shoppingCartPage` and `loginPage` are 
        1. declared in `TestBase`
        2. instantiated by `TestBase`'s using the `pageFactory` instance.
        3. `pageFactory` takes care of implementing these. `TestBase` has a `PageFactory` and by composition `pageFactory` provides the easy methods  `getHomePage()`, `getBreadPage()`, `getDairyPage()`, `getShoppingCartPage()` and `getLoginPage()`.
3. `TestBase` also disposes of the driver in the `tearDown()` method.
4. `TestBase` invokes the `setUp()` and `tearDown()` methods with a test run because the `@BeforeEach` and `@AfterEach` are associated with them respectively. `mvn test` is using junit and `EndToEndTests` has method `public void windowShoppingShouldSucceed()` with the `@Test` annotation. So before that test runs it calls the methods in the package with the `@BeforeEach` and `@AfterEach` annotations. So `TestBase` will create new `driver`, `wait` and `pageFactory` for every method in `EndToEndTests` with the `@Test` annotation and dispose of the driver at the end of each test.

Why is `TestBase` creating a new one of these for each test? I asked Copilot. Apparently it's best practice. It helps with test isolation and avoids flakiness.

>**1. Why Create a New `WebDriver` Per Test?**
>Creating a fresh `WebDriver` instance per test is a common practice in UI test automation. Here’s why:
>
>**Test Isolation**: Each test should ideally run in a clean state, unaffected by other tests. Reusing the same `WebDriver` across tests could lead to state leakage (e.g., open browser tabs, lingering cookies, or corrupted test data).
>
>**Parallel Execution**: If you decide to run tests in parallel in the future, each test will need its own WebDriver instance to avoid conflicts.
>
>However, this practice comes with overhead—starting and stopping a browser for every test is resource-intensive and slows down the test suite.
>
>**2. Why Create `WebDriverWait` and `PageFactory` Per Test?**
>Here’s the current behavior:
>
>`WebDriverWait`: This class depends on the `WebDriver` instance. Since a new `WebDriver` is created per test, you need a new `WebDriverWait` as well to properly reference the new driver.
>
>`PageFactory`: Similarly, the `PageFactory` needs to be tied to the `WebDriver` and `WebDriverWait` instances, so it is recreated each time.
>
>Creating these objects multiple times isn’t inherently bad because they’re lightweight. However, if the logic for initializing them can be centralized and shared, you might simplify the code.
>
>**3. Why Dispose of the Driver After Each Test?**
>As mentioned, this is about test isolation. Quitting the driver ensures:
>
>The browser closes, clearing all state (cookies, session storage, etc.).
>
>Resources are freed properly, avoiding potential memory leaks.
>
>If you reuse the same `WebDriver` instance across tests:
>
>**Pros**: Tests will execute faster since there’s no browser setup overhead.
>
>**Cons**: You risk state leakage between tests, which can lead to flaky or unreliable tests.

I even asked for an example of flakiness that comes with using the same instance across tests.

>```java
>@Test
>public void testA() {
>    driver.get("https://example.com/login");
>    driver.findElement(By.id("username")).sendKeys("userA");
>    driver.findElement(By.id("password")).sendKeys("passwordA");
>    driver.findElement(By.id("login")).click();
>    assertEquals("https://example.com/dashboard", driver.getCurrentUrl());
>}
>
>@Test
>public void testB() {
>    driver.get("https://example.com/login");
>    driver.findElement(By.id("username")).sendKeys("userB");
>    driver.findElement(By.id("password")).sendKeys("passwordB");
>    driver.findElement(By.id("login")).click();
>    assertEquals("https://example.com/dashboard", driver.getCurrentUrl());
>}
>```
>**Problem**: If `testA` leaves the browser logged in, `testB` might fail because it doesn’t start on the login page.

---

## Implementing Page Objects

What is a page object's responsibility?

To have a representation of the elements of the page in code.

As a class, what I've implemented has satisfied the single responsibility principle. In the example below, `BreadPage` 
1. There's a constructor to create the page object.
2. There are locators relevant to a `BreadPage` instance
3. There are methods to interact with the locator.

```java
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
```

Is it good work? Looks like it. The page object provides all that's necessary to find stuff on the page and interact with it.

Here are some notable comments from copilot.

First the strengths.

>### Strengths
>#### Encapsulation of Page-Specific Logic:
>
>The class encapsulates all the logic and locators specific to the "Bread" page. This aligns with the principles of the Page Object Model (POM) by providing a clear and reusable abstraction for interacting with this page.
>
>#### Clear Method Names:
>
>The methods, such as `checkBreadCategoryIsLoaded()` and `addFrenchBaguetteToCart()`, are **well-named** and **expressive**. *They clearly indicate what each method does, improving readability and maintainability.*
>
>#### Use of Static Final Locators:
>
>The locator for the "Add French Baguette to Cart" button is appropriately defined as `private static final`, which ensures that it is **immutable** and *tied to the class rather than instances*. This is efficient and aligns with best practices.
>
>#### Assertion Logic within Page Class:
>
>The `checkBreadCategoryIsLoaded()` method includes an assertion to verify that the correct URL is loaded. This is useful for ensuring that the page is in the expected state.

Then the weaknesses.

>### Weaknesses & Suggestions for Improvement
>#### Dependency on `WebDriver`:
>
>**Issue**: The constructor takes `WebDriver` and `WebDriverWait` directly, **coupling the page class to Selenium’s API**.
>```java
>public class BreadPage { 
>    private WebDriver driver;
>    private WebDriverWait wait;
>    // Other code
>
>    // Constructor
>    public BreadPage(WebDriver driver, WebDriverWait wait) {
>        this.driver = driver;
>        this.wait = wait;      
>    }
>    // Code continues
>```
>
>**Solution**: Refactor the class to depend on a Driver abstraction (as discussed earlier). For example:
>
>```java
>public class BreadPage {
>    private final Driver driver;
>
>    public BreadPage(Driver driver) {
>        this.driver = driver;
>    }
>// more code lower down
>```
<div class="recommendation-box">
Yep. This is a problem and interfaces will need to come in to decouple from Selenium's <code>WebDriver</code> and <code>WebDriverWait</code>. A big lesson here is how tight coupling cascades from <code>TestBase</code> to page objects like <code>BreadPage</code>. Modifying code is going to affect a lot of stuff.
</div><br/>

>### Hardcoding Locators:
>
>**Issue**: The locator for the "Add French Baguette to Cart" button is hardcoded into the class. While this works for now, it could be challenging to maintain if locator strategies change or if the button’s behavior needs localization.
>```java
>public class BreadPage { 
>    // Other code
>    private static final By ADD_FRENCH_BAGUETTE_TO_CART = By.xpath("//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]");
>    // Other Code
>```
>**Solution**: Externalize locators using a configuration file or constants in a separate class. Alternatively, store reusable locators in centralized utilities.
>
<div class="recommendation-box">
One of the criticisms of <code>TestBase</code> was those locators just hanging around. If I were to put them in to a class along with all locators, this wouldn't be a problem.
</div><br/>
<div class="aside-box">
My best guess for localization problems is that I'm identifying these products in English. Same product in a different language would render different innerText. So I'd need to have that different inner text loded into my locator. And I'd want that logic to load that locator from, say a configuration file, into a `Locator` class depending on the language I'm testing in. I.e. check if the <code>html</code> element has attribute <code>lang="{some language code}"</code> and then populate the locators based on that language. In this scenario it makes sense to put all the locators in one place so the Locator("en") instance and Locator("de") instance log the English and German words respectively. It's a clever idea 
</div><br/>
<div class="design-decision">
<h5>Keep locators where they are</h5>
<p>
There are bigger fish to fry and this localization issue isn't even relevant per what I've seen. Why overengineer?
</p>
</div><br/>

>#### Responsibility Overlap:
>
>**Issue**: The `checkBreadCategoryIsLoaded()` method combines two responsibilities: checking the presence of an element and verifying the URL. This violates the Single Responsibility Principle.
>
>**Solution**: Split this into two distinct methods:
>
>`isBreadCategoryElementLoaded()` – Checks the presence of the "Add to Cart" button.
>
>`assertBreadCategoryUrlIsLoaded()` – Verifies the URL matches the expected value.
<div class="design-decision">
<h5>checkBreadCategoryIsLoaded() has two small responsibilities</h5>
The <code>checkBreadCategoryIsLoaded()</code> method combines two responsibilities: checking the presence of an element and verifying the URL. These two responsibilities are just one responsibility to make extra sure the page is loaded. This is achievable in a tiny class. I'm not making new methods to call inside this method just to keep responsibilities segregated. Decision: Keep this method doing one thing well.
</div><br/>

>#### No Handling for Dynamic Waits:
>
>**Issue**: The reliance on `WebDriverWait` assumes static durations for element visibility. If this test scales to complex pages, it could lead to inefficiency or flakiness.
>
>**Solution**: Abstract wait logic into the `Driver` interface so it can adapt dynamically:
>
>```java
>WebElement waitForElement(By locator);
>```
<div class="recommendation-box">
<p>First thought: this recommendation was stupid. It took guesswork to figure out I needed to poll for at least 10 seconds with <code>WebDriverWait</code> to get the tests to work. How do I <em>determine</em> the optimum duration without guesswork? I'd have no problem <em>dynamically</em> updating it if I didn't have to guess.
</p>

<p>
Second thought: the solution isn't even trying to solve that problem. It's actually saying to use an interface, <code>Driver</code> for a driver, which, someday, might solve the problem. This interface contract says method <code>WebElement waitForElement(By locator)</code> must be implemented.
</p>

<p>
I ask if this <code>waitForElement</code> method even exists? It doesn't. I have to make one. I have to make the implementation when I create a class that implements the <code>Driver</code> interface. Below is some sample code:
<br/>
<code>
public class SeleniumDriverManager implements Driver {
    private WebDriver driver;
    private WebDriverWait wait;

    public SeleniumDriverManager() {
        this.driver = new ChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Override
    public WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    @Override
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
</code>
</p>

<p>
Final thought: this isn't the worst idea. It would be nice to have a <code>waitForLocator</code> method available to my page object classes while the implementation of the logic is abstracted. The criticism of dynamically waiting for load times is unfair.
</p>

I want to do this.
</div><br/>

>#### Potential Tight Coupling to ConfigReader:
>
>**Issue**: The `checkBreadCategoryIsLoaded()` method directly uses `ConfigReader.getBaseUri()` to verify the URL. While this centralizes configuration, it creates a dependency on a utility class.
>```java
>// Assert page is loaded
>    public void checkBreadCategoryIsLoaded() {
>        wait.until(ExpectedConditions.presenceOfElementLocated(ADD_FRENCH_BAGUETTE_TO_CART));  
>        String url = driver.getCurrentUrl();
>        assertEquals(ConfigReader.getBaseUri()+"?category=bread", url, "Bread Category did not load as expected.");
>    }
>```
>**Solution**: Pass the `baseUri` as a parameter or derive it dynamically based on the driver’s navigation context. For example:
>
>```java
>String expectedUri = driver.getBaseUri() + "?category=bread";
>assertEquals(expectedUri, url);
>```
<div class="recommendation-box">
I completely agree that I've tightly coupled the ConfigReader to the page object. I just don't know if I'd want it any other way. I'd actuallly do it the same way and just hide this inside a base class <code>BasePage</code> so <code>protected static final String baseUri = ConfigReader.getBaseUri()</code> is available to all the page object that extend <code>BasePage</code>. 
</div><br/>


>### Opportunities for Future Refinement
>#### Reusable Navigation Methods:
>
>If other page classes also require URL assertions, consider abstracting this logic into a Page base class that BreadPage extends. Example:
>
>```java
>public abstract class Page {
>    protected final Driver driver;
>
>    public Page(Driver driver) {
>        this.driver = driver;
>    }
>
>    protected void assertCurrentUrl(String expectedUrl) {
>        assertEquals(expectedUrl, driver.getCurrentUrl());
>    }
>}
>```
<div class="recommendation-box">
<p>I admit I stole my idea for hiding the baseUri from this. 
</p>

<p>
The idea of a <code>BasePage</code> is much more than hiding baseUri. Every page object ineriting this class would have a <code>private Driver driver</code> that's initialised by calling <code>super(Driver driver)</code>. It's a one line change compared to this.driver = driver;
</p>
The logic to assertCurrentUrl is common in every page. It would be clever make the base class implement it. Then the code can call <code>assertCurrentUrl(driver.getCurrentUrl)</code> where the implementation is actually <code>
protected void assertCurrentUrl(String resourcePath)
{
    assertEquals(baseUri+resourcePath, driver.getCurrentUrl());
}
</code>
Having the driver declared in the base of the page allows it to be called in this method. Having the baseUri defined in this base page allows it to be called. The expected path is "" for the root page, "?category=bread" for the bread page, etc. 
</div><br/>

>#### Handling Page State Dynamically:
>
>As your application grows, the `BreadPage` class could handle different states (e.g., the French Baguette is sold out). Design methods to adapt dynamically:
>
>```java
>public boolean isBaguetteAvailable() {
>    return driver.findElements(ADD_FRENCH_BAGUETTE_TO_CART).size() > 0;
>}
>```
<div class="design-decision">
Nice to have. Not interested at this time. No idea how much stock there is.
</div><br/>
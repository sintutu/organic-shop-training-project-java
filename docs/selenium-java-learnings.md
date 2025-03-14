# Selenium Java Learnings

## Selenium Doesn't have access to the Accessibility Tree!

I want to click on the bread link.

![bread link](./images/bread-link.png)

The DOM shows this link as 

```html
<a _ngcontent-c3="" class="list-group-item list-group-item-action" routerlink="/" href="/?category=bread"> Bread </a>
```

Using Playwright I could access this link using [aria-role](https://github.com/sintutu/organic-shop-training-project/blob/8d6026f905b3235ed5016ef9d733029247a0e975/OrganicShopTrainingProjectPlaywrightTests/Pages/HomePage.cs#L14C9-L14C77).

Using Selenium I can't. I must use something else.

How come?

Playwright has access to the Accessibility Tree. Selenium does't. Selenium doesn't recognise implicit roles of anchor tags. :disappointed: 

Implicit roles allow `a` to be associated with aria-role by the browser, thus don't need a developer to specify it. It's actually bad practice as per [ARIA in HTML, W3C Recommendation 13 February 2025](https://www.w3.org/TR/html-aria/?form=MG0AV3#avoid-specifying-redundant-roles).

<div class="recommendation-box">
  By the way,
  <a href="https://www.w3.org/TR/html-aria/?form=MG0AV3#avoid-specifying-redundant-roles">
  ARIA in HTML, W3C Recommendation 13 February 2025
  </a> is awesome guidance to start looking at what comes straight out the browser.
</div>
<br/>

Here's what I learned from copilot:

> Me: Selenium has no automatic aria-role selector in java. Does this mean the browser converts the link (with the implicit aria-role being link) and then the driver can find that link?
>
> Copilot: You’re absolutely on the right track! The browser interprets the HTML elements, applies their implicit roles (like `link` for an `<a>` tag with an `href` attribute), and exposes this information via the **Accessibility Tree**. This Accessibility Tree bridges the gap between the web page and assistive technologies, ensuring proper interpretation of the roles, labels, and other attributes.
>
>However, Selenium doesn’t directly interact with the Accessibility Tree or ARIA roles when locating elements. Instead, it communicates with the browser's DOM (Document Object Model). So, if you’re using something like `By.linkText("Bread")` or `By.xpath("//a[@href='/?category=bread']")`, Selenium is finding the `<a>` element in the DOM based on the specified criteria, rather than by its implicit `link` role.
>
>In summary:
>- The browser does recognize implicit ARIA roles (like `link`) and exposes them to the Accessibility Tree.
>- Selenium interacts with the DOM, not the Accessibility Tree, so its locators (like `findElement`) rely on attributes and structure rather than ARIA roles.
>
>If you’re looking for accessibility testing or working directly with ARIA roles, tools like Axe, Lighthouse, or specialized libraries might come in handy.
>
>Let me know if you'd like further clarification or assistance with Selenium!

So that sucks. I'm not going to be able to easily copy and paste from my Playwright project.

### Why I wanted to use aria-role so much?

I didn't want to change my locator strategy from Playwright so much. I wanted parity. That's the main objective, to get this done fast. And I was lazy. I had the misconception that using Playwright automtically meant I had accessibility testing automated into my tests. In truth, I hadn't. 

If I did want to have accessibility testing, I should probably look the whole page experience rather than just that the single link have an aria-role tag. 

If I really want accessibility testing on the page, I can use [AXE](https://github.com/dequelabs/axe-core). How? I load a page then run the AXE script on it. It scans the page for accessibility issues. Note that it's a node package so I'd need my environment to have node on it. Then I can test a page for accessibility issues somewhat more thoroughly. 

Would I want to put accessibility tests into this particular task? No. I'd probably break the flow of the test as soon as any accessibility problem was found. At a later stage I'd probably want to have accessibility tests run. Would I want it to run in this particular flow? Unsure. You see, I want to make accessibility test for each element that could possibly appear on a page's DOM. Since states of the DOM change (e.g. after selecting a tab on a page and then after the page is loaded is the tab's DOM populated) I'd want to run the same script again. It would dramatically increase the test duration. That would suck.

What's kinda great is how I learnt a Selenium weakness.

## Implementing Waiting with Selenium

Sadly, it takes time for the Bread link to display.

That means this code fails because there's no link to click

```java
        WebElement breadLink = driver.findElement(By.linkText("Bread"));
```

The test run says:
```log
WARNING: Unable to find an exact match for CDP version 134, returning the closest version; found: 133; Please update to a Selenium version that supports CDP version 134
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 4.490 s <<< FAILURE! -- in com.sintutu.organicshopuitests.EndToEndTests
[ERROR] com.sintutu.organicshopuitests.EndToEndTests.endToEndTestShouldSucceed -- Time elapsed: 4.484 s <<< ERROR!
org.openqa.selenium.NoSuchElementException:
no such element: Unable to locate element: {"method":"link text","selector":"Bread"}
```

On the browser side, it takes a bit of time for the Bread link to even appear.

Instead, this code passes:

```java
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement breadLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Bread")));
```

## Finding the `breadLink` 

What does this new code do? I tried figuring out. How can a `WebElement` be set by the `until` method? I looked at the method implementations for `until` and `presenceOfElementLocated` and needed Copilot help for the answer. Here's how it went.

> Me:
> 
> This is the implementation of `until`: 
> 
> ```java
> /**
   > * Repeatedly applies this instance's input value to the given function until one of the following
   > * occurs:
   > *
   > * <ol>
   > *   <li>the function returns neither null nor false
   > *   <li>the function throws an unignored exception
   > *   <li>the timeout expires
   > *   <li>the current thread is interrupted
   > * </ol>
   > *
   > * @param isTrue the parameter to pass to the {@link ExpectedCondition}
   > * @param <V> The function's expected return type.
   > * @return The function's return value if the function returned something different from null or
   > *     false before the timeout expired.
   > * @throws TimeoutException If the timeout expires.
   > */
  > @Override
  > public <V> V until(Function<? super T, V> isTrue) {
    > Instant end = clock.instant().plus(timeout);
> 
    > Throwable lastException;
    > while (true) {
      > try {
        > V value = isTrue.apply(input);
        > if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
          > return value;
        > }
> 
        > // Clear the last exception; if another retry or timeout exception would
        > // be caused by a false or null value, the last exception is not the
        > // cause of the timeout.
        > lastException = null;
      > } catch (Throwable e) {
        > lastException = propagateIfNotIgnored(e);
      > }
> 
      > // Check the timeout after evaluating the function to ensure conditions
      > // with a zero timeout can succeed.
      > if (end.isBefore(clock.instant())) {
        > String message = messageSupplier != null ? messageSupplier.get() : null;
> 
        > String timeoutMessage =
            > String.format(
                > "Expected condition failed: %s (tried for %d second(s) with %d milliseconds"
                    > + " interval)",
                > message == null ? "waiting for " + isTrue : message,
                > timeout.getSeconds(),
                > interval.toMillis());
        > throw timeoutException(timeoutMessage, lastException);
      > }
> 
      > try {
        > sleeper.sleep(interval);
      > } catch (InterruptedException e) {
        > Thread.currentThread().interrupt();
        > throw new WebDriverException(e);
      > }
    > }
  > }
> ``` 
> <div class="aside-box">
> Me (aside):
> 
> The confusing thing about this is how `until` returns `V`, whatever `V` the function passed in is.
></div>
><br/>
> This is the implementation of `presenceOfElementLocated`:
> 
> ```java 
> /**
   > * An expectation for checking that an element is present on the DOM of a page. This does not
   > * necessarily mean that the element is visible.
   > *
   > * @param locator used to find the element
   > * @return the WebElement once it is located
   > */
  > public static ExpectedCondition<WebElement> presenceOfElementLocated(final By locator) {
    > return new ExpectedCondition<WebElement>() {
      > @Override
      > public WebElement apply(WebDriver driver) {
        > return driver.findElement(locator);
      > }
> 
      > @Override
      > public String toString() {
        > return "presence of element located by: " + locator;
      > }
    > };
  > }
> ``` 
> <div class="aside-box">
> Me (aside):
> 
>The function passed into `until` returns an `ExpectedCondition<WebElement>`. Therefore `until` must return an `ExpectedCondition<WebElement>`. So how can the `WebElement breadLink` take on the value of `ExpectedCondition<WebElement>` when they aren't even of the same type?
> 
> That's where I get very confused.
></div>
><br/>
> 
> Here's what Copilot has to say
> 
> CoPilot:
> 
> Great observation, Sintu! Let's dive into this.
> 
>The confusion arises from the expectation that `until` would return an `ExpectedCondition<WebElement>`. Instead, it returns the result of the condition itself. Here's a detailed explanation:
> 
>1. **until Method**: The `until` method is a generic method that waits until the provided condition is met. The condition is a `Function` that takes an input (in this case, the `WebDriver` instance) and returns a value of type `V`. When the condition is met, `until` returns the value produced by the `Function`.
> 
>2. **ExpectedCondition**: `presenceOfElementLocated` is a static method that returns an instance of `ExpectedCondition<WebElement>`. This `ExpectedCondition` is a function that **applies a condition to the `WebDriver` and returns a `WebElement`** when the condition is met.
> 
> When you call `wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Bread")))`, here's what happens step-by-step:
> 
> 1. **Condition Initialization**:
   > ```java
   > ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(By.linkText("Bread"));
   > ```
  > `condition` is now an `ExpectedCondition<WebElement>`. This means **it is a function that takes a `WebDriver` and returns a `WebElement`.**
> 
> 2. **Calling until Method**:
   > ```java
   > WebElement breadLink = wait.until(condition);
   > ```
   > - `wait.until(condition)` waits until the condition is met or the timeout expires.
   > - The `apply` method of `condition` is called repeatedly with the `WebDriver` instance.
   > - When `apply` returns a non-null value (a `WebElement`), `wait.until` returns that `WebElement`.
> 
>The key point is that `until` doesn't return the `ExpectedCondition<WebElement>` itself. Instead, it returns the result of applying that condition, which is a `WebElement`.
> 
>In your case, `breadLink` is assigned the `WebElement` returned by `ExpectedConditions.presenceOfElementLocated(By.linkText("Bread"))`, once the condition is met within the timeout.
> 
> Here's how you can visualize it:
> 
> ```java
> Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
> ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(By.linkText("Bread"));
> WebElement breadLink = wait.until(condition); // breadLink is now the WebElement with link text "Bread"
> ```
> 
> I hope this clarifies things! If you need more examples or further explanation, feel free to ask.

With all that, I put it into my own words.
This code creates a new `WebDriverWait` object called  `wait`. It's instantiated with the `driver` and a duration.

This `wait`, an instance of `WebDriverWait`, has an `until` method that takes in a function as a parameter. `until` runs this function that acts as a condition. What is the condition? The function must return a truthy value within the duration, or else throw a TimeOutException when no truthy value is returned within the duration.

In this case, the function passed in is `ExpectedConditions.presenceOfElementLocated(By.linkText("Bread"))`. It uses the driver to search for the link with text, "Bread". It finds it within the duration, and returns a `WebElement`, a truthy value.

Since the condition is met, `until` will return that truthy value, the returned `WebElement`

And thus the variable `breadLink` is set to this returned value. `breadLink` is there to click.

<div class="aside-box">
On the UI side what was extraordinary is needing to increase the duration of the wait to be 10 seconds for the test to pass. I mean, 10 seconds is a very long time. Wow!
</div>
<br/>

Once found, click it with 

```java
breadlink.click();
```
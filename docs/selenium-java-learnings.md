# Selenium Java Weirdness

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

<div class="fancy-box">
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

### Implementing Waiting with Selenium

Sadly, it takes time for the Bread link to display.

That means this code fails because there's no link to click

```java
        driver.findElement(By.linkText("Bread"));
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
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Bread")));
        driver.findElement(By.linkText("Bread"));
```

What does this new code do?

It creates a new `WebDriverWait` object called  `wait`. This object allows access to the `until(Function<? super T, V> isTrue)` function returns a *truthy* value. The idea is to pass in the function `ExpectedConditions.presenceOfElementLocated(By.linkText("Bread"))` that repeatedly tries to find the Bread link for up to 10 seconds. If it finds the Bread link, it returns the WebElement, a truthy value. That says the link is there to click.

On the UI side what was extraordinary is needing to increase the duration of the wait to be 10 seconds for the test to pass. I mean, 10 seconds is a very long time. Wow!
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
>    * Repeatedly applies this instance's input value to the given function until one of the following
>    * occurs:
>    *
>    * <ol>
>    *   <li>the function returns neither null nor false
>    *   <li>the function throws an unignored exception
>    *   <li>the timeout expires
>    *   <li>the current thread is interrupted
>    * </ol>
>    *
>    * @param isTrue the parameter to pass to the {@link ExpectedCondition}
>    * @param <V> The function's expected return type.
>    * @return The function's return value if the function returned something different from null or
>    *     false before the timeout expired.
>    * @throws TimeoutException If the timeout expires.
>    */
>   @Override
>   public <V> V until(Function<? super T, V> isTrue) {
>     Instant end = clock.instant().plus(timeout);
> 
>     Throwable lastException;
>     while (true) {
>       try {
>         V value = isTrue.apply(input);
>         if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
>           return value;
>         }
> 
>         // Clear the last exception; if another retry or timeout exception would
>         // be caused by a false or null value, the last exception is not the
>         // cause of the timeout.
>         lastException = null;
>       } catch (Throwable e) {
>         lastException = propagateIfNotIgnored(e);
>       }
> 
>       // Check the timeout after evaluating the function to ensure conditions
>       // with a zero timeout can succeed.
>       if (end.isBefore(clock.instant())) {
>         String message = messageSupplier != null ? messageSupplier.get() : null;
> 
>         String timeoutMessage =
>             String.format(
>                 "Expected condition failed: %s (tried for %d second(s) with %d milliseconds"
>                     + " interval)",
>                 message == null ? "waiting for " + isTrue : message,
>                 timeout.getSeconds(),
>                 interval.toMillis());
>         throw timeoutException(timeoutMessage, lastException);
>       }
> 
>       try {
>         sleeper.sleep(interval);
>       } catch (InterruptedException e) {
>         Thread.currentThread().interrupt();
>         throw new WebDriverException(e);
>       }
>     }
>   }
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
>    * An expectation for checking that an element is present on the DOM of a page. This does not
>    * necessarily mean that the element is visible.
>    *
>    * @param locator used to find the element
>    * @return the WebElement once it is located
>    */
>   public static ExpectedCondition<WebElement> presenceOfElementLocated(final By locator) {
>     return new ExpectedCondition<WebElement>() {
>       @Override
>       public WebElement apply(WebDriver driver) {
>         return driver.findElement(locator);
>       }
> 
>       @Override
>       public String toString() {
>         return "presence of element located by: " + locator;
>       }
>     };
>   }
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
> <div class="aside-box">
> Inside `presenceOfEpresenceOfElementLocated`'s implementation there's this `apply` that says to return a `WebElement` as per the locator supplied. Inside `until`'s implementation there is an `apply` that says return what the method returned, which in this case is a `WebElement`.
></div>
><br/>
>
>The key point is that `until` doesn't return the `ExpectedCondition<WebElement>` itself. Instead, it returns the result of `apply`ing that condition, which is a `WebElement`.
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

Once clicked, it's easy to update `currentUrl` and assert that it is the correct one, "https://agular-test-shop-cb70d.firebaseapp.com/?category=bread", with junit's assertEquals method.

## Adding the French Baguette to the cart

This one was hard. While Playwright allowed for filtering, the best option found in Selenium is to use xpath.

The task is to look at all the bread products, of which there is Pita and Fresh French Baguette, and click the Add to cart button on the Fresh French Baguette.

Since xpath looks at the DOM, here's the node where the Fresh French Baguette is found:

```html
<product-card _ngcontent-c2="" _nghost-c4=""><!---->
  <div _ngcontent-c4="" class="card"><!---->
    <img _ngcontent-c4="" class="card-img-top" layout-fill="" src="https://upload.wikimedia.org/wikipedia/commons/f/f5/Baguettes_-_stonesoup.jpg" alt="Fresh French Baguette">
    <div _ngcontent-c4="" class="card-body">
      <h5 _ngcontent-c4="" class="card-title">Fresh French Baguette</h5>
      <p _ngcontent-c4="" class="card-text">$3.00</p>
    </div><!---->
    <div _ngcontent-c4="" class="card-footer"><!---->
      <button _ngcontent-c4="" class="btn btn-secondary btn-block">Add to Cart </button><!---->
    </div>
  </div>
</product-card>
```

The approach to find the xpath is to traverse the dom using javascript. The destination means these two are equivalent expressions:

||XPath|Browser Console|
|:--|:----|:--------------|
|Final Answer|//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]|console.log(Array.from(document.querySelectorAll('product-card')).filter(card => card.querySelector('h5') && card.querySelector('h5').innerText.includes('Fresh French Baguette')).map(card => Array.from(card.querySelectorAll('button')).find(button => button.innerText.includes('Add to Cart'))).filter(button => button !== undefined)|
|W3C schools reference material|[XPath Syntax](https://www.w3schools.com/xml/xpath_syntax.asp) section on Selecting Nodes|[HTML DOM Document querySelectorAll() Method](https://www.w3schools.com/jsref/met_document_queryselectorall.asp)|
|MDN reference material|[XPath functions](https://developer.mozilla.org/en-US/docs/Web/XML/XPath/Reference/Functions)|Nothing useful so far|

Look for all `product-card` elements at *appear anywhere* on the document.

In the browser console you can run
```javascript
const productCards = document.querySelectorAll('product-card');
console.log(productCards);
```

In XPath you run
```xpath
//product-card
```

When searching the whole page, you find two It shows the `product-card`s for the Pita and the Fresh French Baguette.

![All 'product-card' elements on the bread page](./images/bread-product-cards.png).

What separates these `product-card` elements is the text inside one of the descendants. The text, "Fresh French Baguette" exists as a child of the `h5`. So you filter for this to find the product-card elements that contain this value.

You do this in javascript with

```javascript
const filteredProductCards = Array.from(productCards)
                        .filter(card => 
                                card.querySelector('h5') && 
                                card.querySelector('h5')
                                        .innerText.includes('Fresh French Baguette')
                                );
console.log(filteredProductCards);
```

and in XPath with 

```xpath
//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]
```

This is a great spot to highlight XPath's use of the `.` operator in `.//h5`. With the `.` you're telling XPath to look at only the `product-card` elements, not every `h5` tag on the page. Without the `.`, i.e. with `//h5` you'd get every `h5` element on the page.

Now that the correct `product-card` is found, you look for the button. How this is done is 

with javascript

```javascript
const addToCartButtons = filteredProductCards.map(card => 
                                                  Array.from(card.querySelectorAll('button'))
                                                  .find(button => 
                                                        button.innerText.includes('Add to Cart')
                                                        ))
                                             .filter(button => button !== undefined);
console.log(addToCartButtons);
```

and with XPath

```xpath
//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]
```

Note how in javascript there's the possibility to also remove any buttons that are undefined. That doesn't exist in XPath.

And that's how you locate the Add to Cart button on the right product.

It's also how you locate the + and - buttons.

## Removing a block of cheese

Take a similar approach. This time it's traversing a table. 

```html
<table _ngcontent-c6="" class="table">
    <thead _ngcontent-c6="">
    <tr _ngcontent-c6="">
        <th _ngcontent-c6=""></th>
        <th _ngcontent-c6="">Product</th>
        <th _ngcontent-c6="" class="text-center" style="width: 230px">Quantity</th>
        <th _ngcontent-c6="" class="text-right" style="width: 200px">Price</th>
    </tr>
    </thead>
    <tbody _ngcontent-c6=""><!---->
    <tr _ngcontent-c6="">
        <td _ngcontent-c6="">
        <img _ngcontent-c6="" alt="No Image" class="thumbnail" src="https://upload.wikimedia.org/wikipedia/commons/2/25/Maasdam-cheese.jpg"></td>
        <td _ngcontent-c6=""> Cheese </td>
        <td _ngcontent-c6="">
        <product-quantity _ngcontent-c6="" _nghost-c5="">
            <div _ngcontent-c5="" class="row no-gutters">
            <div _ngcontent-c5="" class="col-2">
                <button _ngcontent-c5="" class="btn btn-secondary btn-block">-</button>
            </div>
            <div _ngcontent-c5="" class="col text-center"> 2 in cart </div>
            <div _ngcontent-c5="" class="col-2">
                <button _ngcontent-c5="" class="btn btn-secondary btn-block">+</button>
            </div>
            </div>
        </product-quantity>
        </td>
        <td _ngcontent-c6="" class="text-right"> $24.00 </td>
    </tr>
    </tbody>
    <tfoot _ngcontent-c6="">
    <tr _ngcontent-c6="">
        <th _ngcontent-c6=""></th>
        <th _ngcontent-c6=""></th>
        <th _ngcontent-c6=""></th>
        <th _ngcontent-c6="" class="text-right"> $24.00 </th>
    </tr>
    </tfoot>
</table>
```

First find the table. To do this is a matter of searching the whole document for every table element. *Since this page only has the one table this is fine.*

Do this in Javascript with

```javascript
const tables = document.querySelectorAll('table');
console.log(tables); // This will log all tables on the page
```

Do this in XPath with

```xpath
//table
```

The next bit is to find that `<tr>` that contains `<td>" Cheese "</td>`.

There are two approaches. Since the assumption is that there are multiple possible tables, create a 3D array where the outermost array is of `<table>` and the middle array is of `<tr>` elements from each table and the inner array is of `<td>` elements. Make the outermost loop go over the `<table>` elements. Make the middle loop go through the `<tr>` elements. In this innermost loop create an array of `<td>` elements inside the `<tr>` element, and search this array for some `<td>` element which contains the trimmed innerText "Cheese".

This Javascript this is how it works

```javascript
const tables = document.querySelectorAll('table');
console.log(tables); // Logs all tables on the page

// Create a nested array of rows for each table
const tableRows = Array.from(tables).map(table => {
    const rows = Array.from(table.querySelectorAll('tr'));
    console.log(rows); // Log all rows in this table
    return rows;
});

console.log(tableRows); // Logs the nested array of rows for each table

// Find the row containing "Cheese" in the nested structure
let cheeseRow;
for (const rows of tableRows) { // Iterate through each table's rows
    cheeseRow = rows.find(row => {
        console.log(row.innerText); // Log each row's text content
        return Array.from(row.querySelectorAll('td'))
            .some(td => td.innerText.trim() === 'Cheese'); // Match row with exact "Cheese" text
    });
    if (cheeseRow) break; // Stop searching once the row is found
}

console.log(cheeseRow); // Logs the row containing "Cheese" (or undefined if not found)

// Find the decrement button within the matched row
if (cheeseRow) {
    const decrementButton = Array.from(cheeseRow.querySelectorAll('button')).find(button =>
        button.innerText === '-' // Ensures we only get the "decrement" button
    );
    console.log(decrementButton); // Logs the `-` button

    // Click the button if found
    if (decrementButton) {
        decrementButton.click();
        console.log("Clicked the decrement button for 'Cheese'.");
    } else {
        console.log("Decrement button not found in the Cheese row.");
    }
} else {
    console.log("Cheese row not found.");
}
```

Something special about printing a table row with `console.log(tableRows)` is that it prints all the text in the row. That's because it prints the innerText of the `<tr>` element, which is the text of all the children in the order the text appears.

Below is the table row containing cheese. Notice how the text of the children
```html
<tr _ngcontent-c2="">
  <td _ngcontent-c2="">
  <img _ngcontent-c2="" alt="No Image" class="thumbnail" src="https://upload.wikimedia.org/wikipedia/commons/2/25/Maasdam-cheese.jpg"></td>
  <td _ngcontent-c2=""> Cheese </td>
  <td _ngcontent-c2="">
    <product-quantity _ngcontent-c2="" _nghost-c3="">
      <div _ngcontent-c3="" class="row no-gutters">
        <div _ngcontent-c3="" class="col-2">
          <button _ngcontent-c3="" class="btn btn-secondary btn-block">-</button>
        </div>
      <div _ngcontent-c3="" class="col text-center"> 5 in cart 
      </div>
      <div _ngcontent-c3="" class="col-2">
        <button _ngcontent-c3="" class="btn btn-secondary btn-block">+</button>
      </div>
    </div>
  </product-quantity>
  </td>
  <td _ngcontent-c2="" class="text-right"> $60.00 </td>
</tr>
```
which means you get printed to the console
![Console printout of cheese table row](./images/console-printout-of-cheese-table-row.png)

and digging into the table row shows you how it really does have that content as innerText:

![innerText of row with Cheese](./images/table-row-inner-text.png)

There's a different, somewhat faster way with `flatmap`. It's 2D. This approach still looks at all tables on the document (of which there is only one) and makes it the outer array. The inner array is only of `<tr>` elements that have `<td>` elements whose trimmed innerText is "Cheese". And because it's a flatmap it just returns a single array of all elements that satisfy this condition. It so happens there's only one element that satisfies the condition. With this 1D array, it's easier to iterate over.

```javascript
const tables = document.querySelectorAll('table');
console.log(tables); // Logs all tables on the page

const flatRow = Array.from(tables)
  .flatMap(table =>
    Array.from(table.querySelectorAll('tr'))
      .find(row =>
            Array.from(row.querySelectorAll('td'))
      .some(td => td.innerText.trim() === 'Cheese'))
  ); 
console.log(flatRow); // Returns array of all `<td>` elements where some element has innerText.trim() === 'Cheese'
const cheeseRow = flatRow[0]; // get the first matching row
console.log(cheeseRow); // Logs the row containing "Cheese"

// Find the decrement button within the matched row
const decrementButton = cheeseRow
  ? Array.from(cheeseRow.querySelectorAll('button')).find(button => button.innerText === '-')
  : undefined;

console.log(decrementButton); // Logs the `-` button

// Click the decrement button if found
if (decrementButton) {
  decrementButton.click();
  console.log("Clicked the decrement button for 'Cheese'.");
} else {
  console.log("Decrement button not found.");
}
```


>Copilot:
>
>### What Does `flatMap` Do and Why Is It Used?
>
>#### Purpose of `flatMap`:
>`flatMap` is a combination of:
>- **`map()`**, which transforms (or maps) each element of an array, and
>- **`flat()`**, which flattens (or merges) nested arrays into a single array.
>
>For example:
>```javascript
>const array = [[1, 2], [3, 4]];
>const flattened = array.flatMap(x => x.map(y => y * 2)); // [2, 4, 6, 8]
>```
> ### The `find` Method
>
>#### Purpose:
>The `find` method is used to search an array for the **first element** that matches a specified condition (also known as a predicate). It stops searching as soon as it finds the first match and returns that element. If no match is found, it returns `undefined`.
>
>#### **Syntax**:
>```javascript
>array.find(callback(element, index, array), thisArg)
>```
>
>- **`callback`**: A function executed on each element in the array. It should return `true` for the matching element.
>- **Parameters of `callback`**:
>  - `element`: The current element being processed.
>  - `index` (optional): The index of the current element.
>  - `array` (optional): The entire array being processed.
>- **`thisArg` (optional)**: A value to use as `this` when executing the `callback`.
>
>#### **Example**:
>```javascript
>const numbers = [10, 20, 30, 40];
>const foundNumber = numbers.find(num => num > 25);
>console.log(foundNumber); // Output: 30
>```
>
>- In this case, the `find` method stops as soon as it finds the first number (`30`) greater than `25`.
>
>
>### The `some` Method
>
>#### Purpose:
>The `some` method is used to check whether **at least one element** in the array satisfies a specified condition (predicate). It stops searching as soon as it finds a matching element and returns `true`. If no elements match, it returns `false`.
>
>#### Syntax:
>```javascript
>array.some(callback(element, index, array), thisArg)
>```
>
>- **`callback`**: A function executed on each element in the array. It should return `true` for the condition to be satisfied.
>- **Parameters of `callback`**:
>  - `element`: The current element being processed.
>  - `index` (optional): The index of the current element.
>  - `array` (optional): The entire array being processed.
>- **`thisArg` (optional)**: A value to use as `this` when executing the `callback`.
>
>#### **Example**:
>```javascript
>const numbers = [10, 20, 30, 40];
>const hasLargeNumbers = numbers.some(num => num > 35);
>console.log(hasLargeNumbers); // Output: true
>```
>
>- Here, the `some` method checks if at least one number is greater than `35`. Since `40` matches, it returns `true`.
>
>### **Comparison Between `find` and `some`**
>
>| **Feature**        | **`find`**                              | **`some`**                           |
>|---------------------|-----------------------------------------|---------------------------------------|
>| **Purpose**         | Finds the first matching element.       | Checks if **any** element matches.    |
>| **Return Value**    | The matching element, or `undefined`.   | A boolean (`true` or `false`).        |
>| **Search Stops?**   | Yes, after the first match.             | Yes, after the first match.           |
>| **Use Case**        | Retrieve the actual matching element.   | Check if a condition is ever `true`.  |
>
>---
>
>### **How They Work in Your Code**
>
>#### **`find` Example (Locate "Cheese" Row):**
>```javascript
>const cheeseRow = Array.from(tables).flatMap(table =>
>  Array.from(table.querySelectorAll('tr')).find(row =>
>    Array.from(row.querySelectorAll('td')).some(td => td.>innerText.trim() === 'Cheese')
>  )
>)[0];
>```
>
>- **`find` in Action:**
>  - It scans each table’s rows (`<tr>`) for the **first row** where the condition is satisfied.
>  - The condition checks if **any `<td>` within the row** has text content trimmed to "Cheese."
>
>- **`some` in Action:**
>  - Inside the `find` callback, `some` checks whether **at least one `<td>` in the row** matches the condition.
>
>#### Simplified Breakdown:
>1. **`some`:**
>   ```javascript
>   Array.from(row.querySelectorAll('td')).some(td => td.innerText.trim() === 'Cheese');
>   ```
>   - Checks if any `<td>` cell in a row has `"Cheese"` as its exact text (after trimming).
>
>2. **`find`:**
>   ```javascript
>   rows.find(row => /* condition using some */);
>   ```
>   - Locates the **first row** where the `some` condition is `true`.

Then find the table row that contains the table data that contains the inner text Cheese. What happens if I have other items in the cart like "Blue Cheese Quiche" or "Cheesecake"? This is why there's a trim. " Cheese ", when trimmed, will resolve to "Cheese" while the others won't. The approach is to find in a flatmap just that element.

In Javascript that's 

```javascript
const tables = document.querySelectorAll('table');
console.log(tables); // This will log all tables on the page
const cheeseRow = Array.from(tables)
  .flatMap(table => {
           const rows = Array.from(table.querySelectorAll('tr'));
           console.log(rows); // Log all rows in the current table
           const matchedRow = rows.find(row => {
                              console.log(row.innerText); // Log each row's text content
                              return Array.from(row.querySelectorAll('td'))
                              .some(td => td.innerText.trim() === 'Cheese');
                              });
           console.log(matchedRow); // Log the matched row, or `undefined` if not found
           return matchedRow;
           })[0]; // Get the first matching row, if any has Cheese with or without whitespace
console.log(cheeseRow); // Logs the row containing inner text " Cheese "
const decrementButton = Array.from(cheeseRow.querySelectorAll('button'))
                        .find(button =>
                              button.innerText === '-' // Ensures we only get the "decrement" button
                             );
console.log(decrementButton); // Logs the `-` button
```

What's interesting about this approach is how it uses `flatmap`. A table is an array of rows, each row being an array of table data. What `flatmap` does is put all the content into a single array.  Another concern is how flat


and in XPath that's 

```xpath
//table//tr[td[normalize-space(text()) = 'Cheese']]//button[text() = '-']
```

Lastly, find the - button.

In Javascript it's

```javascript
const decrementButton = Array.from(cheeseRow.querySelectorAll('button')).find(button =>
    button.innerText === '-' // Ensures we only get the "decrement" button
);
console.log(decrementButton); // Logs the `-` button
```

and in XPath it's

```xpath
//table//tr[td[contains(text(), 'Cheese')]]//button[text() = '-']
```
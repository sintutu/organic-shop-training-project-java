||XPath|Browser Console|
|:--|:----|:--------------|
|Final Answer|//product-card[.//h5[contains(text(), 'Fresh French Baguette')]]//button[contains(text(), 'Add to Cart')]|console.log(Array.from(document.querySelectorAll('product-card')).filter(card => card.querySelector('h5') && card.querySelector('h5').innerText.includes('Fresh French Baguette')).map(card => Array.from(card.querySelectorAll('button')).find(button => button.innerText.includes('Add to Cart'))).filter(button => button !== undefined)|
|W3C schools reference material|[XPath Syntax](https://www.w3schools.com/xml/xpath_syntax.asp) section on Selecting Nodes|[HTML DOM Document querySelectorAll() Method](https://www.w3schools.com/jsref/met_document_queryselectorall.asp)|
|MDN reference material|[XPath functions](https://developer.mozilla.org/en-US/docs/Web/XML/XPath/Reference/Functions)|Nothing useful so far|

```javascript
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
```javascript

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
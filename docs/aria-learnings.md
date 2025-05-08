# Aria Learnings

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

# Markdown Styling with Markdown Preview Enhanced

I really like [ARIA in HTML, W3C Recommendation 13 February 2025](https://www.w3.org/TR/html-aria/?form=MG0AV3#avoid-specifying-redundant-roles). I wanted to highlight how much I like this page inside [selenium-java-learnings.md](selenium-java-learnings.md). I wanted to add some styling.

To do this I needed to 
1. Put my love for the page into some HTML tags
2. Add styling to the HTML tags.

For the first part, I did this:
```html
<div class="fancy-box">
  By the way,
  <a href="https://www.w3.org/TR/html-aria/?form=MG0AV3#avoid-specifying-redundant-roles">
  ARIA in HTML, W3C Recommendation 13 February 2025
  </a> is awesome guidance to start looking at what comes straight out the browser.
</div>
<br/>
```

What this does is it puts my happiness inside a `div` element with a `class` called `"fancy-box"`. That allows me to apply `css` styes `div` for the class named `.fancy-box`...so long as I have some `.css` file to reference. 

So how do I get that `.css` file? Enter the VS Code Extension [Markdown Preview Enhanced](https://marketplace.visualstudio.com/items?itemName=shd101wyy.markdown-preview-enhanced). 
I went to the command palette and selected `Markdown Preview Enhanced: Customize CSS (Workspace)`. This allowed me to add to *repo root* the custom styling I wanted for this particular project. How does this work? It adds the following:

1. A directory at repo root called `.crossnote`
2. Child files:
  1. `.crossnote/config.js`
  2. `.crossnote/head.html`
  3. `.crossnote/parser.js`
  4. `.crossnote/style.less`

The cool bit is the `style.less`. That file is 

I asked copilot:

> Me: What's the difference between css and less
>
> Copilot: CSS is the base language for styling, while LESS enhances it with extra features that make it more efficient, modular, and easier to maintain. If your projects involve a lot of repetitive styles, LESS can be a huge time-saver!
>
>|Feature	   |CSS	                           |LESS                                        |
>|:------------|:------------------------------|:-------------------------------------------|
>|Type	       |Standard stylesheet language   |Preprocessor for CSS                        |
>|Syntax	   |Plain CSS	                   |Adds variables, nesting, and mixins         |
>|Compilation  |No compilation needed	       |Needs to be compiled into CSS               |
>|Reusability  |Limited	                       |Highly reusable through variables and mixins|
>|Readability  |Can become verbose	           |Cleaner and more structured due to nesting  |
>
>You can define variables e.g. @main-color
>```less
>@main-color: #3498db;
>h1 {
>    color: @main-color;
>}
>```
>
>You can define functions and use them inside other styles with [mixins](https://lesscss.org/?form=MG0AV3#mixins)
>```less
>.rounded-corners (@radius: 5px) {
>    border-radius: @radius;
>}
>div {
>    .rounded-corners(10px);
>}
>```
>
>You can nest properties rather than expressing them explicitly. 
> less does it like
>```less
>nav {
>    ul {
>        margin: 0;
>        li {
>            list-style: none;
>       }
>   }
>}
>
>rather than
>```css
>nav ul {
>    margin: 0;
>}
>nav ul li {
>    list-style: none;
>}
>```

So in `.crossnote/style.less` I updated `.markdown-preview.markdown-preview`'s implementation the with copilot's styling code suggestion
```less
.markdown-preview.markdown-preview {
  // modify your style here
  // eg: background-color: blue;
  .fancy-box {
    background-color: #d4f7d4; // Light green background
    border: 2px solid #228b22; // Dark green border
    border-radius: 15px; // Rounded corners
    padding: 10px; // Space inside the box
    font-family: Arial, sans-serif; // Clean font
    color: #006400; // Text color
    position: relative; // Enables pseudo-element positioning
  }

  .fancy-box:before {
    content: "😊"; // Adds the smiley
    position: absolute;
    top: -10px;
    left: -10px;
    font-size: 20px;
  }
}
```

I got the result I wanted. Pretty much.

I also got the markdown emoji :disappointed: rendering properly in VS Code. How will this work in GitHub? We'll see.

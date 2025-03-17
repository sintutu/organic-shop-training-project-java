package com.sintutu.organicshopuitests;

import org.junit.jupiter.api.Test;
import com.sintutu.organicshopuitests.utils.TestBase;

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
public class EndToEndTests extends TestBase {
    @Test
    public void endToEndTestShouldSucceed(){

        //Navigate to home page
        pageFactory.getHomePage().load();
        pageFactory.getHomePage().assertHomePageIsLoaded();

        //1. Select the Bread Category
        pageFactory.getHomePage().clickBreadLink();

        //2. Assert that the URL has changed
        pageFactory.getBreadPage().assertBreadCategoryIsLoaded();

        //3. Add a French Baguette
        pageFactory.getBreadPage().addFrenchBaguetteToCart();

        // 4. Select the Dairy Category
        pageFactory.getHomePage().clickDairyLink();

        // 5. Assert that the URL has changed
        pageFactory.getDairyPage().assertDairyCategoryIsLoaded();

        // 6. Add 2 blocks of cheese
        pageFactory.getDairyPage().addCheeseToCart();
        pageFactory.getDairyPage().incrementCheeseInCart();

        // 7. Open the cart
        goToShoppingCart();

        // 8. Assert that the URL has changed
        pageFactory.getShoppingCartPage().assertShopppingCartPageIsLoaded();

        // 9. Remove one block of cheese
        pageFactory.getShoppingCartPage().decrementCheeseFromCart();

        // 10. Click checkout
        pageFactory.getShoppingCartPage().goToCheckOut();
        pageFactory.getLoginPage().assertLoginPageIsLoadedViaCheckout();

        // 11. Click Login
        goToLoginPage();
        pageFactory.getLoginPage().assertLoginPageIsLoaded();
    }
}

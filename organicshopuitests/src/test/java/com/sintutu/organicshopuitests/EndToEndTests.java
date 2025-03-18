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
    public void windowShoppingShouldSucceed(){

        //Navigate to home page
        homePage.load();
        homePage.checkHomePageIsLoaded();

        //1. Select the Bread Category
        homePage.clickBreadLink();

        //2. Assert that the URL has changed
        breadPage.checkBreadCategoryIsLoaded();

        //3. Add a French Baguette
        breadPage.addFrenchBaguetteToCart();

        // 4. Select the Dairy Category
        homePage.clickDairyLink();

        // 5. Assert that the URL has changed
        dairyPage.checkDairyCategoryIsLoaded();

        // 6. Add 2 blocks of cheese
        dairyPage.addCheeseToCart();
        dairyPage.addOneMoreCheeseToCart();

        // 7. Open the cart
        clickShoppingCartLink();

        // 8. Assert that the URL has changed
        shoppingCartPage.checkShopppingCartPageIsLoaded();

        // 9. Remove one block of cheese
        shoppingCartPage.removeOneCheeseFromCart();

        // 10. Click checkout
        shoppingCartPage.goToCheckOut();
        loginPage.checkLoginPageIsLoadedViaCheckout();

        // 11. Click Login
        clickLoginLink();
        loginPage.checkLoginPageIsLoaded();
    }
}

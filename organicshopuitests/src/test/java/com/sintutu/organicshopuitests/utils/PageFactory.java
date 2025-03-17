package com.sintutu.organicshopuitests.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sintutu.organicshopuitests.pages.BreadPage;
import com.sintutu.organicshopuitests.pages.DairyPage;
import com.sintutu.organicshopuitests.pages.HomePage;
import com.sintutu.organicshopuitests.pages.LoginPage;
import com.sintutu.organicshopuitests.pages.ShoppingCartPage;

public class PageFactory {
    private WebDriver driver;
    private WebDriverWait wait;

    private HomePage homePage;
    private BreadPage breadPage;
    private DairyPage dairyPage;
    private ShoppingCartPage shoppingCartPage;
    private LoginPage loginPage;

    public PageFactory(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public HomePage getHomePage() {
        if (homePage == null) {
            homePage = new HomePage(driver, wait);
        }
        return homePage;
    }

    public BreadPage getBreadPage() {
        if (breadPage == null) {
            breadPage = new BreadPage(driver, wait);
        }
        return breadPage;
    }

    public DairyPage getDairyPage() {
        if (dairyPage == null) {
            dairyPage = new DairyPage(driver, wait);
        }
        return dairyPage;
    }

    public ShoppingCartPage getShoppingCartPage() {
        if (shoppingCartPage == null) {
            shoppingCartPage = new ShoppingCartPage(driver, wait);
        }
        return shoppingCartPage;
    }

    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver, wait);
        }
        return loginPage;
    }
}
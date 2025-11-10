package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    // Locators - these are commonly used; adjust if site changes
    private By flightsMenu = By.xpath("//span[text()='Flights' or contains(text(),'Flights')]");
    private By closeLoginPopup = By.cssSelector("li[data-cy='account']"); // sometimes clicking outside closes it

    public HomePage(WebDriver driver) { super(driver); }

    public void open(String url) {
        driver.get(url);
    }

    public void goToFlights() {
        // try to click flights tab
        try {
            click(flightsMenu);
        } catch (Exception e) {
            // fallback: try JS click
            jsClick(flightsMenu);
        }
    }

    public void closePossibleLoginPopup() {
        // Common approach: click on the body or on account li to close overlay
        try {
            driver.findElement(closeLoginPopup).click();
        } catch (Exception ignored) {}
    }
}

package com.example.tests;

import com.example.pages.*;
import com.example.pages.models.Flight;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class FlightSearchTest {
    private WebDriver driver;
    private HomePage home;
    private FlightsPage flights;
    private ResultsPage results;

    @BeforeClass
    public void setup() {
        driver = com.example.pages.BrowserFactory.createChrome();
        home = new HomePage(driver);
        flights = new FlightsPage(driver);
        results = new ResultsPage(driver);
    }

    @Test
    public void searchAndPrintCheapestFlights() {
        // 1. Navigate
        home.open("https://www.makemytrip.com/");
        home.closePossibleLoginPopup();
        home.goToFlights();

        // 2. Enter From/To - change cities as required for your test
        flights.enterFrom("Bengaluru");   // example: Bangalore/Bengaluru
        flights.enterTo("Delhi");         // example: Delhi

        // 3. Select a date in the next month (pick day 10 of next month as example)
        java.time.LocalDate nextMonth = java.time.LocalDate.now().plusMonths(1);
        int dayToPick = Math.min(10, nextMonth.lengthOfMonth()); // pick 10th or last day if too short
        flights.selectDateNextMonth(dayToPick);

        // 4. Click Search
        flights.clickSearch();

        // 5. Wait some seconds for results to load and print top two
        try { Thread.sleep(8000); } catch (InterruptedException ignored) {}
        results.printTopTwo();

        // 6. Extra: open new tab and navigate to Google
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
        java.util.ArrayList<String> tabs = new java.util.ArrayList<>(driver.getWindowHandles());
        String newTab = tabs.get(tabs.size() - 1);
        driver.switchTo().window(newTab);
        driver.get("https://www.google.com");
        System.out.println("New tab title: " + driver.getTitle());

        // extra scenario: return to results tab
        driver.switchTo().window(tabs.get(0));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}

package com.example.pages;

import com.example.pages.models.Flight;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.*;
import java.util.stream.Collectors;

public class ResultsPage extends BasePage {

    // Locators for flight result items - these will likely need updating if MakeMyTrip changes classes.
    // Common pattern: each result row has price in an element with class containing 'price' or 'actual-price'
    private By flightRows = By.cssSelector("div.fli-list"); // example; fallback used below
    private By priceElementSelector = By.cssSelector(".priceSection .actual-price"); // common pattern
    private By airlineNameSelector = By.cssSelector(".airlineInfo .airways"); // example
    private By departTimeSelector = By.cssSelector(".dept .time");
    private By arriveTimeSelector = By.cssSelector(".reaching .time");

    public ResultsPage(WebDriver driver) { super(driver); }

    /**
     * Collect flights shown on results page. This method tries several locator patterns
     * and parses price text into integers. Returns a list of Flight objects.
     */
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();

        // Try common flight row patterns; adapt if site different.
        List<WebElement> rows = driver.findElements(By.cssSelector("div.fli-list, li[data-test-attrib='flight-card'], .listingCard, .resultCard"));

        if (rows == null || rows.isEmpty()) {
            // fallback: detect any big card with price inside
            rows = driver.findElements(By.xpath("//div[.//span[contains(@class,'actual-price') or contains(@class,'price')]]"));
        }

        for (WebElement row : rows) {
            try {
                String rawPrice = "";
                try {
                    WebElement priceEl = row.findElement(By.xpath(".//span[contains(@class,'actual-price') or contains(@class,'price')]"));
                    rawPrice = priceEl.getText();
                } catch (Exception e) {
                    // try alternative
                    rawPrice = row.getText();
                }

                int price = parsePrice(rawPrice);

                String airline = "Unknown";
                try {
                    WebElement air = row.findElement(By.xpath(".//p[contains(@class,'airways') or contains(@class,'airlineName') or contains(@class,'airline')]"));
                    airline = air.getText();
                } catch (Exception ignored) {}

                String depart = "";
                try { depart = row.findElement(By.xpath(".//div[contains(@class,'dept') or contains(@class,'departure')]//p")).getText(); } catch (Exception ignored) {}
                String arrive = "";
                try { arrive = row.findElement(By.xpath(".//div[contains(@class,'reaching') or contains(@class,'arrival')]//p")).getText(); } catch (Exception ignored) {}

                flights.add(new Flight(airline, depart, arrive, price, row.getText()));
            } catch (Exception e) {
                // skip problematic rows
            }
        }

        return flights;
    }

    private int parsePrice(String raw) {
        if (raw == null) return Integer.MAX_VALUE;
        // remove non-digit characters
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return Integer.MAX_VALUE;
        try { return Integer.parseInt(digits); }
        catch (NumberFormatException e) { return Integer.MAX_VALUE; }
    }

    /**
     * Find the cheapest and second cheapest flights and print them.
     */
    public void printTopTwo() {
        List<Flight> flights = getAllFlights().stream()
                .filter(f -> f.getPrice() < Integer.MAX_VALUE)
                .sorted(Comparator.comparingInt(Flight::getPrice))
                .collect(Collectors.toList());

        if (flights.isEmpty()) {
            System.out.println("No flights found or couldn't parse prices. Check locators and network.");
            return;
        }

        System.out.println("Cheapest flight: " + flights.get(0));
        if (flights.size() >= 2) {
            System.out.println("Second cheapest flight: " + flights.get(1));
        } else {
            System.out.println("Only one flight found.");
        }
    }
}

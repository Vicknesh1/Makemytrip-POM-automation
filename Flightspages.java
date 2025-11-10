package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class FlightsPage extends BasePage {
    private By fromInput = By.id("fromCity");
    private By toInput = By.id("toCity");

    // After typing in field, suggestions appear as <p> elements with city name text; we select suggestion using contains text
    private String citySuggestionXpath = "//p[contains(text(),'%s')]";

    // date picker opener (often clicking departure date input)
    private By departDateInput = By.xpath("//label[@for='departure']//span[contains(@class,'lbl_input')]|//span[text()='DEPARTURE']");

    // Calendar next button
    private By calendarNext = By.cssSelector(".DayPicker-NavButton--next");

    // month title element inside calendar
    private By monthTitle = By.cssSelector(".DayPicker-Caption");

    private By searchButton = By.xpath("//a[contains(@class,'primaryBtn') and (text()='Search' or contains(text(),'Search'))]");

    public FlightsPage(WebDriver driver) {
        super(driver);
    }

    public void enterFrom(String city) {
        click(fromInput);
        // focus on the input field and type
        WebElement inp = driver.findElement(By.xpath("//input[@placeholder='From' or @placeholder='From (city, airport)']"));
        inp.clear();
        inp.sendKeys(city);
        waitShort();
        // choose first suggestion which contains city
        click(By.xpath(String.format(citySuggestionXpath, city)));
    }

    public void enterTo(String city) {
        click(toInput);
        WebElement inp = driver.findElement(By.xpath("//input[@placeholder='To' or @placeholder='To (city, airport)']"));
        inp.clear();
        inp.sendKeys(city);
        waitShort();
        click(By.xpath(String.format(citySuggestionXpath, city)));
    }

    /**
     * Selects a date in the next month. If today's date is 2025-11-10 and we want a day X next month, pick next month automatically.
     * @param dayOfMonth target day in next month (1..31)
     */
    public void selectDateNextMonth(int dayOfMonth) {
        // open date picker
        try {
            click(departDateInput);
        } catch (Exception e) {
            // alternate click on common date input
            jsClick(By.cssSelector("label[for='departure']"));
        }

        // compute target month name and year
        LocalDate now = LocalDate.now();
        LocalDate target = now.plusMonths(1);
        String targetMonthName = target.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String targetYear = String.valueOf(target.getYear());

        // wait for month to appear
        int attempts = 0;
        while (attempts < 6) {
            try {
                String caption = waitForVisible(monthTitle).getText(); // e.g., "December 2025"
                if (caption.toLowerCase().contains(targetMonthName.toLowerCase()) 
                    && caption.contains(targetYear)) {
                    break;
                } else {
                    click(calendarNext);
                    waitShort();
                }
            } catch (Exception ex) {
                // try clicking next and continue
                click(calendarNext);
                waitShort();
            }
            attempts++;
        }

        // now pick the day cell
        // the calendar may use <div aria-label="Fri Dec 12 2025"> or <p>day text; use flexible xpath
        String dayXpathOptions[] = new String[] {
            "//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))]//p[text()='%d']",
            "//div[contains(@aria-label,'%s %d') and not(contains(@class,'disabled'))]",
            "//div[contains(@class,'dateInnerCell')][.//p[text()='%d']]"
        };

        boolean selected = false;
        // try direct matching with p[text()=day]
        try {
            click(By.xpath(String.format(dayXpathOptions[0], dayOfMonth)));
            selected = true;
        } catch (Exception ignored) {}

        if (!selected) {
            // try aria-label pattern with month name
            try {
                String aria = String.format("%s %d", targetMonthName.substring(0,3), dayOfMonth); // e.g., Dec 12
                // better: full month name
                click(By.xpath(String.format("//div[contains(@aria-label,'%s %d')]", targetMonthName, dayOfMonth)));
                selected = true;
            } catch (Exception ignored) {}
        }

        if (!selected) {
            // fallback: click any visible day with the text
            try {
                click(By.xpath("//p[text()='" + dayOfMonth + "']"));
                selected = true;
            } catch (Exception e) {
                throw new RuntimeException("Unable to select calendar date; please revise locator.");
            }
        }
    }

    public void clickSearch() {
        click(searchButton);
    }
}

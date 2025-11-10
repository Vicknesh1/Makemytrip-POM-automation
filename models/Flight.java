package com.example.pages.models;

public class Flight {
    private String airline;
    private String departure;
    private String arrival;
    private int price; // price in integer rupees or chosen currency
    private String elementText; // raw capture if needed

    public Flight(String airline, String departure, String arrival, int price, String elementText) {
        this.airline = airline;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.elementText = elementText;
    }

    public String getAirline() { return airline; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public int getPrice() { return price; }
    public String getElementText() { return elementText; }

    @Override
    public String toString() {
        return String.format("%s | %s -> %s | ₹%d", airline, departure, arrival, price);
    }
}

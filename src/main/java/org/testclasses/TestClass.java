package org.testclasses;

import java.time.Instant;

public class TestClass {
    @Property(name = "numberProperty")
    private String stringProperty;
    private int myNumber;
    private Instant timeProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }
}

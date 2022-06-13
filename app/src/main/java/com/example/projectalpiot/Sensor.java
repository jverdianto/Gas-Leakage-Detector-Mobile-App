package com.example.projectalpiot;

public class Sensor {
    private String name;
    private String led;
    private boolean gasLeak;
    private boolean fan;

    public Sensor(){}

    public Sensor(String name, String led, boolean gasLeak, boolean fan) {
        this.name = name;
        this.led = led;
        this.gasLeak = gasLeak;
        this.fan = fan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLed() {
        return led;
    }

    public void setLed(String led) {
        this.led = led;
    }

    public boolean isGasLeak() {
        return gasLeak;
    }

    public void setGasLeak(boolean gasLeak) {
        this.gasLeak = gasLeak;
    }

    public boolean isFan() {
        return fan;
    }

    public void setFan(boolean fan) {
        this.fan = fan;
    }
}

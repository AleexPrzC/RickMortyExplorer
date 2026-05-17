package com.example.rickmortyexplorer.model;

public class DeviceStatus {
    private int batteryLevel;
    private boolean charging;
    private String recommendation;
    private long timestamp;

    public DeviceStatus() {
    }

    public DeviceStatus(int batteryLevel, boolean charging, String recommendation, long timestamp) {
        this.batteryLevel = batteryLevel;
        this.charging = charging;
        this.recommendation = recommendation;
        this.timestamp = timestamp;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public boolean isCharging() {
        return charging;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

package com.fusi.fishingalarm.wifi;

public class WifiElement {

    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;
    private String bssid;
    private boolean connact;

    public boolean isConnact() {
        return connact;
    }

    public void setConnact(boolean connact) {
        this.connact = connact;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

}

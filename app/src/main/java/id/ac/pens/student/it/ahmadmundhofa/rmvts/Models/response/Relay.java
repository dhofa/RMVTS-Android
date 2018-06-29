package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relay {
    @SerializedName("gps")
    @Expose
    private Boolean gps;
    @SerializedName("ignition")
    @Expose
    private Boolean ignition;
    @SerializedName("vibration")
    @Expose
    private Boolean vibration;
    @SerializedName("buzzer")
    @Expose
    private Boolean buzzer;

    public Boolean getGps() {
        return gps;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public Boolean getIgnition() {
        return ignition;
    }

    public void setIgnition(Boolean ignition) {
        this.ignition = ignition;
    }

    public Boolean getVibration() {
        return vibration;
    }

    public void setVibration(Boolean vibration) {
        this.vibration = vibration;
    }

    public Boolean getBuzzer() {
        return buzzer;
    }

    public void setBuzzer(Boolean buzzer) {
        this.buzzer = buzzer;
    }

}

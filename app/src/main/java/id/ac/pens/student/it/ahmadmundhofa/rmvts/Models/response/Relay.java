package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relay {
    @SerializedName("ignition_off")
    @Expose
    private Boolean ignitionOff;
    @SerializedName("ignition_on")
    @Expose
    private Boolean ignitionOn;
    @SerializedName("vibration")
    @Expose
    private Boolean vibration;
    @SerializedName("buzzer")
    @Expose
    private Boolean buzzer;
    @SerializedName("realtime_gps")
    @Expose
    private Boolean realtimeGps;

    public Boolean getIgnitionOff() {
        return ignitionOff;
    }

    public void setIgnitionOff(Boolean ignitionOff) {
        this.ignitionOff = ignitionOff;
    }

    public Boolean getIgnitionOn() {
        return ignitionOn;
    }

    public void setIgnitionOn(Boolean ignitionOn) {
        this.ignitionOn = ignitionOn;
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

    public Boolean getRealtimeGps() {
        return realtimeGps;
    }

    public void setRealtimeGps(Boolean realtimeGps) {
        this.realtimeGps = realtimeGps;
    }

}

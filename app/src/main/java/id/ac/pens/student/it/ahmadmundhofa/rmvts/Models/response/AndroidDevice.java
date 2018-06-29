package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AndroidDevice {
    @SerializedName("last_latitude")
    @Expose
    private Double lastLatitude;
    @SerializedName("last_longitude")
    @Expose
    private Double lastLongitude;

    public Double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(Double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }
}

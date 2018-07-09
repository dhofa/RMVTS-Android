package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastImageCapture {
    @SerializedName("last_driver_photos")
    @Expose
    private String lastDriverPhotos;
    @SerializedName("time")
    @Expose
    private String time;

    public String getLastDriverPhotos() {
        return lastDriverPhotos;
    }

    public void setLastDriverPhotos(String lastDriverPhotos) {
        this.lastDriverPhotos = lastDriverPhotos;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

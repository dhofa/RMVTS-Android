package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataResponse {
    @SerializedName("vehicle_data")
    @Expose
    private VehicleData vehicleData;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("id_raspberry")
    @Expose
    private String idRaspberry;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("koordinat")
    @Expose
    private List<Koordinat> koordinat = null;
    @SerializedName("driver")
    @Expose
    private List<Driver> driver = null;
    @SerializedName("relay")
    @Expose
    private Relay relay;

    public Relay getRelay() {
        return relay;
    }

    public void setRelay(Relay relay) {
        this.relay = relay;
    }

    public List<Driver> getDriver() {
        return driver;
    }

    public void setDriver(List<Driver> driver) {
        this.driver = driver;
    }

    public List<Koordinat> getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(List<Koordinat> koordinat) {
        this.koordinat = koordinat;
    }

    public String getIdRaspberry() {
        return idRaspberry;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIdRaspberry(String idRaspberry) {
        this.idRaspberry = idRaspberry;
    }
    public VehicleData getVehicleData() {
        return vehicleData;
    }

    public void setVehicleData(VehicleData vehicleData) {
        this.vehicleData = vehicleData;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataResponse {
    @SerializedName("vehicle_data")
    @Expose
    private VehicleData vehicleData;
    @SerializedName("relay")
    @Expose
    private Relay relay;
    @SerializedName("android_device")
    @Expose
    private AndroidDevice androidDevice;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("koordinat")
    @Expose
    private List<Koordinat> koordinat = null;
    @SerializedName("driver")
    @Expose
    private List<Object> driver = null;
    @SerializedName("vibration")
    @Expose
    private List<Vibration> vibration = null;
    @SerializedName("ignition")
    @Expose
    private List<Ignition> ignition = null;
    @SerializedName("buzzer")
    @Expose
    private List<Buzzer> buzzer = null;
    @SerializedName("log_activity")
    @Expose
    private List<LogActivity> logActivity = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public VehicleData getVehicleData() {
        return vehicleData;
    }

    public void setVehicleData(VehicleData vehicleData) {
        this.vehicleData = vehicleData;
    }

    public Relay getRelay() {
        return relay;
    }

    public void setRelay(Relay relay) {
        this.relay = relay;
    }

    public AndroidDevice getAndroidDevice() {
        return androidDevice;
    }

    public void setAndroidDevice(AndroidDevice androidDevice) {
        this.androidDevice = androidDevice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Koordinat> getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(List<Koordinat> koordinat) {
        this.koordinat = koordinat;
    }

    public List<Object> getDriver() {
        return driver;
    }

    public void setDriver(List<Object> driver) {
        this.driver = driver;
    }

    public List<Vibration> getVibration() {
        return vibration;
    }

    public void setVibration(List<Vibration> vibration) {
        this.vibration = vibration;
    }

    public List<Ignition> getIgnition() {
        return ignition;
    }

    public void setIgnition(List<Ignition> ignition) {
        this.ignition = ignition;
    }

    public List<Buzzer> getBuzzer() {
        return buzzer;
    }

    public void setBuzzer(List<Buzzer> buzzer) {
        this.buzzer = buzzer;
    }

    public List<LogActivity> getLogActivity() {
        return logActivity;
    }

    public void setLogActivity(List<LogActivity> logActivity) {
        this.logActivity = logActivity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}

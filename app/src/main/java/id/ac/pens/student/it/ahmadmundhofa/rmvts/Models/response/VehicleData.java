package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleData {
    @SerializedName("last_state_ignition")
    @Expose
    private LastStateIgnition lastStateIgnition;
    @SerializedName("last_state_vibration")
    @Expose
    private LastStateVibration lastStateVibration;
    @SerializedName("last_location")
    @Expose
    private LastLocation lastLocation;
    @SerializedName("last_image_capture")
    @Expose
    private LastImageCapture lastImageCapture;
    @SerializedName("user_photos")
    @Expose
    private String userPhotos;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("plate_number")
    @Expose
    private String plateNumber;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;

    public LastStateVibration getLastStateVibration() {
        return lastStateVibration;
    }

    public void setLastStateVibration(LastStateVibration lastStateVibration) {
        this.lastStateVibration = lastStateVibration;
    }

    public LastLocation getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(LastLocation lastLocation) {
        this.lastLocation = lastLocation;
    }

    public LastImageCapture getLastImageCapture() {
        return lastImageCapture;
    }

    public void setLastImageCapture(LastImageCapture lastImageCapture) {
        this.lastImageCapture = lastImageCapture;
    }

    public LastStateIgnition getLastStateIgnition() {
        return lastStateIgnition;
    }

    public void setLastStateIgnition(LastStateIgnition lastStateIgnition) {
        this.lastStateIgnition = lastStateIgnition;
    }

    public String getUserPhotos() {
        return userPhotos;
    }

    public void setUserPhotos(String userPhotos) {
        this.userPhotos = userPhotos;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}

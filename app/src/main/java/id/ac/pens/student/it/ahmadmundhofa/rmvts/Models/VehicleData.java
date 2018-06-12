package id.ac.pens.student.it.ahmadmundhofa.rmvts.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleData {
    @SerializedName("last_state_ignition")
    @Expose
    private LastStateIgnition lastStateIgnition;
    @SerializedName("last_latitude")
    @Expose
    private Integer lastLatitude;
    @SerializedName("last_longitude")
    @Expose
    private Integer lastLongitude;
    @SerializedName("user_photos")
    @Expose
    private String userPhotos;
    @SerializedName("last_driver_photos")
    @Expose
    private String lastDriverPhotos;
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

    public LastStateIgnition getLastStateIgnition() {
        return lastStateIgnition;
    }

    public void setLastStateIgnition(LastStateIgnition lastStateIgnition) {
        this.lastStateIgnition = lastStateIgnition;
    }

    public Integer getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Integer lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Integer getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(Integer lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getUserPhotos() {
        return userPhotos;
    }

    public void setUserPhotos(String userPhotos) {
        this.userPhotos = userPhotos;
    }

    public String getLastDriverPhotos() {
        return lastDriverPhotos;
    }

    public void setLastDriverPhotos(String lastDriverPhotos) {
        this.lastDriverPhotos = lastDriverPhotos;
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

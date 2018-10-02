package id.ac.pens.student.it.ahmadmundhofa.rmvts.Utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by dhofa on 1/16/2018.
 */

public class MarkerModel implements ClusterItem {

    private final LatLng mPosition;

    private double latitude;

    private double longitude;

    private String title;

    private Integer jarak;

    private Integer radius;

    private String id_marker;

    private BitmapDescriptor icon;

    public MarkerModel(double lat, double lng, String MyTitle, Integer Radius, String id) {
        mPosition = new LatLng(lat, lng);
        latitude = lat;
        longitude= lng;
        title = MyTitle;
        radius = Radius;
        id_marker=id;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    public String getId_marker() {
        return id_marker;
    }

    public Integer getJarak() {
        return jarak;
    }

    public void setJarak(Integer jarak) {
        this.jarak = jarak;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return title;
    }

    public Integer getRadius() {
        return radius;
    }

    public Double getLatitude(){ return latitude;}

    public Double getLongitude(){ return longitude;}

    public String getSnippet() {
        return null;
    }
}

package ps.put.wiferadar.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize
@JsonSerialize
public class Location {
    private double latitude, longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

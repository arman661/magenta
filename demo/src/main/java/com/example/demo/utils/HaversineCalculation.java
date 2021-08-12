package com.example.demo.utils;

import com.example.demo.entity.City1;

public class HaversineCalculation {

    private HaversineCalculation() {}

    public static Double calculateDistance(City1 cityA, City1 cityB) {
        Double latitudeA = cityA.getLatitude();
        Double longitudeA = cityA.getLongitude();
        Double latitudeB = cityB.getLatitude();
        Double longitudeB = cityB.getLongitude();
        double theta = longitudeA - longitudeB;
        double dist = Math.sin(deg2rad(latitudeA)) * Math.sin(deg2rad(latitudeB)) + Math.cos(deg2rad(latitudeA)) * Math.cos(deg2rad(latitudeB)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

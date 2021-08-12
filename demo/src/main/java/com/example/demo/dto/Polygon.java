package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Polygon {
    private String type;
    private List<List<List<Double>>> coordinates;
//    private Crs crs;

    public Polygon() {
        coordinates = new ArrayList<>();
        coordinates.add(new ArrayList<>());
        type = "Polygon";
//        crs = new Crs();
    }

    public void addPoint(Double latitude, Double longitude) {
        List<Double> point = Arrays.asList(latitude, longitude);
        coordinates.get(0).add(point);
    }

}

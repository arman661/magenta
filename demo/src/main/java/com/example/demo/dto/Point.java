package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Point {
    private String type;
    private List<Double> coordinates;

    public Point() {
        coordinates = new ArrayList<>();
        type = "Point";
    }

    public void addCityPoint(Double longitude, Double latitude) {
        coordinates = Arrays.asList(longitude, latitude);
    }
}

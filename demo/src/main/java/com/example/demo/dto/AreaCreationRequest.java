package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AreaCreationRequest {
    String name;
    List<GeoPoint> points = new ArrayList<>();
}

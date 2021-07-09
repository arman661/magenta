package com.example.demo.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CityAndDistanceCreationRequest {
    List<CityCreationRequest> cityCreationRequests = new ArrayList<>();
    List<DistanceCreationRequest> distanceCreationRequests = new ArrayList<>();
}

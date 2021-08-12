package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CityAndDistanceCreationRequest {
    List<CityCreationRequest> cityCreationRequests;
    List<DistanceCreationRequest> distanceCreationRequests;
}

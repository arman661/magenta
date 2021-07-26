package com.example.demo.controller;

import com.example.demo.request.CityCreationRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AreaCreationRequest {
    String name;
    List<AreaCreationRequestList> areaCreationRequests = new ArrayList<>();
}

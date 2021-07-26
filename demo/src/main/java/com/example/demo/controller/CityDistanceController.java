package com.example.demo.controller;

import com.example.demo.*;
import com.example.demo.request.CityAndDistanceCreationRequest;
import com.example.demo.request.DistanceCreationRequest;
import com.example.demo.response.CalculationResponse;
import com.example.demo.response.CityResponse;
import com.example.demo.service.CityDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CityDistanceController {
    @Autowired
    private CityDistanceService cityDistanceService;

    @PostMapping(
            path = { "/rest/distance"},
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
            )
    public ResponseEntity createDistance(@RequestBody CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        return cityDistanceService.createCitiesAndDistancesList(cityAndDistanceCreationRequest);

    }

    @PostMapping(
            path = {"/create-area"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
        )
    public ResponseEntity createArea(@RequestBody AreaCreationRequest areaCreationRequest) {
        return cityDistanceService.createArea(areaCreationRequest);
    }


    @GetMapping("/rest/city")
    public ResponseEntity<List<CityResponse>> getCities() {
        return cityDistanceService.getAllCities();
    }

    @GetMapping("/rest/calculation")
    public ResponseEntity<List<CalculationResponse>> calculateDistances(
            @RequestParam CalculationType calculationType,
            @RequestParam List<String> fromCities,
            @RequestParam List<String> toCities
    ) {
      return cityDistanceService.calculateDistance(calculationType, fromCities, toCities);
    }

}

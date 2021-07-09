package com.example.demo;

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
    public ResponseEntity createDistance(@RequestBody List<DistanceCreationRequest> distanceCreationRequestList) {
        return cityDistanceService.createDistanceList(distanceCreationRequestList);
    }

    @GetMapping("/rest/city")
    public ResponseEntity<List<CityResponse>> getCities() {
        return cityDistanceService.getAllCities();
    }

    @GetMapping("/rest/distance")
    public ResponseEntity<List<Distance>> getDistance() {
        return cityDistanceService.getDistance();
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

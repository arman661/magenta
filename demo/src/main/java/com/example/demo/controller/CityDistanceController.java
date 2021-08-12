package com.example.demo.controller;

import com.example.demo.*;
import com.example.demo.dto.CityAndDistanceCreationRequest;
import com.example.demo.dto.CalculationResponse;
import com.example.demo.dto.CityResponse;
import com.example.demo.service.CityDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public void createDistance(@RequestBody CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        cityDistanceService.createCitiesAndDistancesList(cityAndDistanceCreationRequest);

    }




    @GetMapping("/rest/city")
    public ResponseEntity<List<CityResponse>> getCities() {
        return ResponseEntity.ok(cityDistanceService.getAllCities());
    }

    @GetMapping("/rest/calculation")
    public ResponseEntity<List<CalculationResponse>> calculateDistances(
            @RequestParam CalculationType calculationType,
            @RequestParam List<String> fromCities,
            @RequestParam List<String> toCities
    ) {
        try {
            List<CalculationResponse> calculationResultList = cityDistanceService
                    .calculateDistance(calculationType, fromCities, toCities);
            return ResponseEntity.status(HttpStatus.OK).body(calculationResultList);
        } catch (CannotBeCalculatedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

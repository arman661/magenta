package com.example.demo.controller;

import com.example.demo.*;
import com.example.demo.dto.*;
import com.example.demo.entity.Area;
import com.example.demo.service.CityDistanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CityDistanceController {
    private final CityDistanceService cityDistanceService;

    public CityDistanceController(CityDistanceService cityDistanceService) {
        this.cityDistanceService = cityDistanceService;
    }

    @PostMapping(
            path = { "/rest/distance"},
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public void createDistance(@RequestBody CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        cityDistanceService.createCitiesAndDistancesList(cityAndDistanceCreationRequest);
    }

    @PostMapping(
            path = {"/create-area"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void createArea(@RequestBody AreaCreationRequest areaCreationRequest) {
        cityDistanceService.createArea(areaCreationRequest);
    }

//    @PostMapping(
//            path = {"/rest/create-city"},
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity createCity(@RequestBody CityCreationRequest cityCreationRequest) {
//
//        return cityDistanceService.createCity(cityCreationRequest);
//    }

    @DeleteMapping("rest/area/{id}")
    public ResponseEntity<Area> deleteArea(@PathVariable("id") String id) {
        try{
            Long longId = Long.parseLong(id.toString());
            if (longId <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return cityDistanceService.deleteArea(longId);
        }
        catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/rest/area/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable("id") String id, @RequestBody AreaUpdateRequest areaUpdateRequest) {
        try {
            Long longId = Long.parseLong(id.toString());
            if (longId <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return cityDistanceService.updateArea(longId, areaUpdateRequest);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/rest/cityAreas")
    public ResponseEntity<List<CityFromAreasResponse>> getAreasContainsCity(@RequestParam String city) {
        return cityDistanceService.getAreasContainsArea(city);
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

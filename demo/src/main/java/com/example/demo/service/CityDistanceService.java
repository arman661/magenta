package com.example.demo.service;

import com.example.demo.*;
import com.example.demo.entity.City;
import com.example.demo.entity.Distance;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.DistanceRepository;
import com.example.demo.request.CityAndDistanceCreationRequest;
import com.example.demo.request.CityCreationRequest;
import com.example.demo.request.DistanceCreationRequest;
import com.example.demo.response.CalculationResponse;
import com.example.demo.response.CityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.demo.CalculationType.*;

@Service
public class CityDistanceService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DistanceRepository distanceRepository;

    public ResponseEntity<List<CityResponse>> getAllCities() {
        return ResponseEntity.ok(StreamSupport
                .stream(cityRepository.findAll().spliterator(), false)
                .map(city -> new CityResponse(city.getId(), city.getName()))
                .collect(Collectors.toList()));

    }

    public ResponseEntity<List<CalculationResponse>> calculateDistance(CalculationType calculationType, List<String> fromCities, List<String> toCities) {
        List<CalculationResponse> result = new ArrayList<>();
        ArrayList<City> fromCitiesList = new ArrayList<>();
        ArrayList<City> toCitiesList = new ArrayList<>();
        for (String fromCity : fromCities) {
            Optional<City> byName = cityRepository.findByName(fromCity);
            if (byName.isPresent()) {
                fromCitiesList.add(byName.get());
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        for (String toCity : toCities) {
            Optional<City> byName = cityRepository.findByName(toCity);
            if (byName.isPresent()) {
                toCitiesList.add(byName.get());
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        if (calculationType == DISTANCE_MATRIX || calculationType == ALL) {
            for (City city : fromCitiesList) {
                for (City city1 : toCitiesList) {
                    Optional<Distance> distanceOptional = distanceRepository.findByFromCityIdAndToCityId(city.getId(), city1.getId());
                    if (!distanceOptional.isPresent()) {
                        throw new CannotBeCalculatedException("Not found distance for fromCity " + city.getName() + " and toCity " + city1.getName());
                    }
                    result.add(new CalculationResponse(
                            DISTANCE_MATRIX,
                            city.getName(),
                            city1.getName(),
                            distanceOptional.get().getDistance()));
                }
            }
        }
        if (calculationType == CROWFLIGHT || calculationType == ALL) {
            for (City city : fromCitiesList) {
                for (City city1 : toCitiesList) {

                    result.add(new CalculationResponse(
                            CROWFLIGHT,
                            city.getName(),
                            city1.getName(),
                            calculateDistanceInner(city, city1)));
                }
            }
        }
        return ResponseEntity.ok(result);

    }

    private Double calculateDistanceInner(City cityA, City cityB) {
        Double latitudeA = cityA.getLatitude();
        Double longitudeA = cityA.getLongitude();
        Double latitudeB = cityB.getLatitude();
        Double longitudeB = cityB.getLongitude();
        double theta = longitudeA - longitudeB;
        double dist = Math.sin(deg2rad(latitudeA)) * Math.sin(deg2rad(latitudeB)) + Math.cos(deg2rad(latitudeA)) * Math.cos(deg2rad(latitudeB)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public ResponseEntity<List<Distance>> getDistance() {
        return ResponseEntity.ok(StreamSupport
                .stream(distanceRepository.findAll().spliterator(), false)
                .collect(Collectors.toList()));
    }

    public ResponseEntity createDistance(DistanceCreationRequest distanceCreationRequest) {
        Distance distance = new Distance();
        Optional<City> fromCityOptional = cityRepository.findByName(distanceCreationRequest.getFromCity());
        if (fromCityOptional.isPresent()) {
            distance.setFromCityId(fromCityOptional.get().getId());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<City> toCityOptional = cityRepository.findByName(distanceCreationRequest.getToCity());
        if (toCityOptional.isPresent()) {
            distance.setToCityId(toCityOptional.get().getId());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        distance.setDistance(distanceCreationRequest.getDistance());

        Distance save = distanceRepository.save(distance);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity createDistanceList(List<DistanceCreationRequest> distanceCreationRequestList) {
        for (DistanceCreationRequest distanceCreationRequest : distanceCreationRequestList) {
            ResponseEntity response = createDistance(distanceCreationRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return response;
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity createCitiesAndDistancesList(CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        for (CityCreationRequest cityCreationRequest : cityAndDistanceCreationRequest.getCityCreationRequests()) {
            City city = new City(
                    cityCreationRequest.getName(),
                    cityCreationRequest.getLatitude(),
                    cityCreationRequest.getLongitude()
            );
            cityRepository.save(city);
        }
        for (DistanceCreationRequest distanceCreationRequest : cityAndDistanceCreationRequest.getDistanceCreationRequests()) {
            ResponseEntity response = createDistance(distanceCreationRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return response;
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

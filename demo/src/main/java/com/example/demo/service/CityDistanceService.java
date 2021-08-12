package com.example.demo.service;

import com.example.demo.*;
import com.example.demo.entity.City1;
import com.example.demo.entity.Distance1;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.DistanceRepository;
import com.example.demo.dto.CityAndDistanceCreationRequest;
import com.example.demo.dto.CityCreationRequest;
import com.example.demo.dto.DistanceCreationRequest;
import com.example.demo.dto.CalculationResponse;
import com.example.demo.dto.CityResponse;
import com.example.demo.utils.HaversineCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.demo.CalculationType.*;

@Service
public class CityDistanceService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DistanceRepository distanceRepository;

    public List<CityResponse> getAllCities() {
        return StreamSupport
                .stream(cityRepository.findAll().spliterator(), false)
                .map(city -> new CityResponse(city.getId(), city.getName()))
                .collect(Collectors.toList());

    }

    public List<CalculationResponse> calculateDistance(CalculationType calculationType, List<String> fromCitiesNames
            , List<String> toCitiesName) throws CannotBeCalculatedException {
        List<CalculationResponse> result = new ArrayList<>();
        List<City1> fromCitiesList = findCities(fromCitiesNames);
        List<City1> toCitiesList = findCities(toCitiesName);

        for (City1 from : fromCitiesList) {
            for (City1 to : toCitiesList) {
                if (calculationType == DISTANCE_MATRIX || calculationType == ALL) {
                    result.add(calculateViaDistanceTable(from, to));
                }
                if (calculationType == CROWFLIGHT || calculationType == ALL) {
                    result.add(calculateHaversineFunction(from, to));
                }
            }
        }

        return result;
    }

    private CalculationResponse calculateHaversineFunction(City1 from, City1 to) {
        return new CalculationResponse(
                CROWFLIGHT,
                from.getName(),
                to.getName(),
                HaversineCalculation.calculateDistance(from, to));
    }

    private CalculationResponse calculateViaDistanceTable(City1 from, City1 to) throws CannotBeCalculatedException {
        Optional<Distance1> distanceOptional = distanceRepository.findByFromCityIdAndToCityId(from.getId(), to.getId());
        if (!distanceOptional.isPresent()) {
            throw new CannotBeCalculatedException("Not found distance for fromCity " + from.getName() + " and toCity " + to.getName());
        }
        return new CalculationResponse(
                DISTANCE_MATRIX,
                from.getName(),
                to.getName(),
                distanceOptional.get().getDistance());
    }

    private List<City1> findCities(List<String> fromCities) throws CannotBeCalculatedException {
        List<City1> resultList = new ArrayList<>();
        for (String fromCity : fromCities) {
            Optional<City1> byName = cityRepository.findByName(fromCity);
            if (byName.isPresent()) {
                resultList.add(byName.get());
            } else {
                throw new CannotBeCalculatedException(fromCity);
            }
        }
        return resultList;
    }

    public Optional<Long> createDistance(DistanceCreationRequest distanceCreationRequest) {
        Distance1 distance = new Distance1();
        Optional<City1> fromCityOptional = cityRepository.findByName(distanceCreationRequest.getFromCity());
        if (fromCityOptional.isPresent()) {
            distance.setFromCityId(fromCityOptional.get().getId());
        } else {
            return Optional.empty();
        }
        Optional<City1> toCityOptional = cityRepository.findByName(distanceCreationRequest.getToCity());
        if (toCityOptional.isPresent()) {
            distance.setToCityId(toCityOptional.get().getId());
        } else {
            return Optional.empty();
        }

        distance.setDistance(distanceCreationRequest.getDistance());

        Distance1 save = distanceRepository.save(distance);
        return Optional.ofNullable(save.getId());
    }

    public void createCitiesAndDistancesList(CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        for (CityCreationRequest cityCreationRequest : cityAndDistanceCreationRequest.getCityCreationRequests()) {
            City1 city = new City1(
                    cityCreationRequest.getName(),
                    cityCreationRequest.getLatitude(),
                    cityCreationRequest.getLongitude()
            );
            cityRepository.save(city);
        }
        for (DistanceCreationRequest distanceCreationRequest : cityAndDistanceCreationRequest.getDistanceCreationRequests()) {
            createDistance(distanceCreationRequest);
        }
    }

}

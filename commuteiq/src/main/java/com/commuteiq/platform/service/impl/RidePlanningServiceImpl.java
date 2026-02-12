package com.commuteiq.platform.service.impl;

import com.commuteiq.platform.dto.response.RidePlanResponse;
import com.commuteiq.platform.entity.*;
import com.commuteiq.platform.exception.InvalidOperationException;
import com.commuteiq.platform.repository.*;
import com.commuteiq.platform.service.RidePlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart Pooling Engine — groups pending ride requests into optimized ride
 * plans.
 *
 * Algorithm:
 * 1. Fetch all PENDING ride requests for the target date
 * 2. Load commute preferences for each employee
 * 3. Sort requests by pickup window start time
 * 4. Greedily cluster requests using:
 * - Haversine distance between employee homes (< configurable threshold)
 * - Overlapping pickup time windows
 * - Same-gender constraint when required by any member
 * - Vehicle capacity limit
 * 5. Assign available vehicles and drivers to each cluster
 * 6. Compute estimated distance/duration and stop order
 * 7. Persist RidePlan + RidePlanEmployee records, mark requests as PLANNED
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RidePlanningServiceImpl implements RidePlanningService {

    private final RideRequestRepository rideRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CommutePreferenceRepository commutePreferenceRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final RidePlanRepository ridePlanRepository;
    private final RidePlanEmployeeRepository ridePlanEmployeeRepository;

    @Value("${app.pooling.distance-threshold-km:2.0}")
    private double distanceThresholdKm;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public List<RidePlanResponse> generatePlansForDate(LocalDate date) {
        log.info("Starting ride plan generation for date: {}", date);

        // 1. Fetch all PENDING requests for the date
        List<RideRequest> pendingRequests = rideRequestRepository
                .findByStatusAndRequestDate(RideRequestStatus.PENDING, date);

        if (pendingRequests.isEmpty()) {
            log.info("No pending requests found for date: {}", date);
            return Collections.emptyList();
        }

        // 2. Build a lookup of employee -> preference
        Map<Long, CommutePreference> preferenceMap = new HashMap<>();
        for (RideRequest req : pendingRequests) {
            Long empId = req.getEmployee().getId();
            commutePreferenceRepository.findByEmployeeId(empId)
                    .ifPresent(pref -> preferenceMap.put(empId, pref));
        }

        // 3. Sort requests by pickup start time (employees without preference go last)
        pendingRequests.sort(Comparator.comparing(req -> {
            CommutePreference pref = preferenceMap.get(req.getEmployee().getId());
            return pref != null ? pref.getPickupStartTime() : java.time.LocalTime.MAX;
        }));

        // 4. Greedy clustering
        List<List<RideRequest>> clusters = buildClusters(pendingRequests, preferenceMap);

        // 5. Assign vehicles and drivers
        List<Vehicle> availableVehicles = vehicleRepository.findByActiveTrue();
        List<Driver> availableDrivers = driverRepository.findByActiveTrue();

        // Filter out vehicles/drivers already assigned for this date
        Set<Long> usedVehicleIds = ridePlanRepository.findByDate(date).stream()
                .map(rp -> rp.getVehicle().getId())
                .collect(Collectors.toSet());
        Set<Long> usedDriverIds = ridePlanRepository.findByDate(date).stream()
                .map(rp -> rp.getDriver().getId())
                .collect(Collectors.toSet());

        availableVehicles = availableVehicles.stream()
                .filter(v -> !usedVehicleIds.contains(v.getId()))
                .collect(Collectors.toList());
        availableDrivers = availableDrivers.stream()
                .filter(d -> !usedDriverIds.contains(d.getId()))
                .collect(Collectors.toList());

        if (availableVehicles.isEmpty() || availableDrivers.isEmpty()) {
            throw new InvalidOperationException("No available vehicles or drivers for date: " + date);
        }

        // 6. Create RidePlans
        List<RidePlanResponse> results = new ArrayList<>();
        int vehicleIndex = 0;
        int driverIndex = 0;

        for (List<RideRequest> cluster : clusters) {
            if (vehicleIndex >= availableVehicles.size() || driverIndex >= availableDrivers.size()) {
                log.warn("Ran out of vehicles/drivers. {} clusters remain unassigned.",
                        clusters.size() - results.size());
                break;
            }

            // Find a vehicle with enough capacity
            Vehicle vehicle = null;
            for (int i = vehicleIndex; i < availableVehicles.size(); i++) {
                if (availableVehicles.get(i).getCapacity() >= cluster.size()) {
                    vehicle = availableVehicles.get(i);
                    availableVehicles.remove(i);
                    break;
                }
            }

            if (vehicle == null) {
                // Use any available vehicle even if smaller (split may be needed)
                vehicle = availableVehicles.get(vehicleIndex++);
            }

            Driver driver = availableDrivers.get(driverIndex++);

            // Compute estimated distance (sum of Haversine distances between consecutive
            // stops)
            double totalDistance = computeRouteDistance(cluster);
            double estimatedDuration = totalDistance / 30.0 * 60.0; // Assume 30 km/h avg → minutes

            RidePlan ridePlan = RidePlan.builder()
                    .vehicle(vehicle)
                    .driver(driver)
                    .date(date)
                    .estimatedDistance(Math.round(totalDistance * 100.0) / 100.0)
                    .estimatedDuration(Math.round(estimatedDuration * 100.0) / 100.0)
                    .status(RidePlanStatus.SCHEDULED)
                    .build();

            RidePlan savedPlan = ridePlanRepository.save(ridePlan);

            // Create RidePlanEmployee entries with stop order
            List<RidePlanResponse.RidePlanEmployeeResponse> employeeResponses = new ArrayList<>();
            for (int i = 0; i < cluster.size(); i++) {
                RideRequest req = cluster.get(i);
                RidePlanEmployee rpe = RidePlanEmployee.builder()
                        .ridePlan(savedPlan)
                        .employee(req.getEmployee())
                        .stopOrder(i + 1)
                        .build();
                ridePlanEmployeeRepository.save(rpe);

                // Mark the ride request as PLANNED
                req.setStatus(RideRequestStatus.PLANNED);
                rideRequestRepository.save(req);

                employeeResponses.add(RidePlanResponse.RidePlanEmployeeResponse.builder()
                        .employeeId(req.getEmployee().getId())
                        .employeeName(req.getEmployee().getName())
                        .stopOrder(i + 1)
                        .build());
            }

            results.add(RidePlanResponse.builder()
                    .id(savedPlan.getId())
                    .vehicleId(vehicle.getId())
                    .vehiclePlateNumber(vehicle.getPlateNumber())
                    .driverId(driver.getId())
                    .driverName(driver.getName())
                    .date(date)
                    .estimatedDistance(savedPlan.getEstimatedDistance())
                    .estimatedDuration(savedPlan.getEstimatedDuration())
                    .status(savedPlan.getStatus().name())
                    .employees(employeeResponses)
                    .createdAt(savedPlan.getCreatedAt())
                    .build());
        }

        log.info("Generated {} ride plans for date: {}", results.size(), date);
        return results;
    }

    /**
     * Greedy clustering algorithm.
     * For each unassigned request, try to add it to an existing cluster if:
     * - It is within the distance threshold of the cluster centroid
     * - Its pickup window overlaps with the cluster's window
     * - Gender constraints are satisfied
     * - Cluster hasn't exceeded a reasonable max size (will be bounded by vehicle
     * capacity later)
     */
    private List<List<RideRequest>> buildClusters(List<RideRequest> requests,
            Map<Long, CommutePreference> preferenceMap) {
        List<List<RideRequest>> clusters = new ArrayList<>();
        boolean[] assigned = new boolean[requests.size()];
        int maxClusterSize = 8; // Will be further limited by vehicle capacity

        for (int i = 0; i < requests.size(); i++) {
            if (assigned[i])
                continue;

            List<RideRequest> cluster = new ArrayList<>();
            cluster.add(requests.get(i));
            assigned[i] = true;

            Employee anchor = requests.get(i).getEmployee();
            CommutePreference anchorPref = preferenceMap.get(anchor.getId());

            for (int j = i + 1; j < requests.size(); j++) {
                if (assigned[j])
                    continue;
                if (cluster.size() >= maxClusterSize)
                    break;

                Employee candidate = requests.get(j).getEmployee();
                CommutePreference candidatePref = preferenceMap.get(candidate.getId());

                // Check distance
                double distance = haversineDistance(
                        anchor.getHomeLatitude(), anchor.getHomeLongitude(),
                        candidate.getHomeLatitude(), candidate.getHomeLongitude());

                if (distance > distanceThresholdKm)
                    continue;

                // Check pickup window overlap
                if (!pickupWindowsOverlap(anchorPref, candidatePref))
                    continue;

                // Check gender constraint
                if (!genderConstraintSatisfied(cluster, candidate, preferenceMap))
                    continue;

                // Check same office location
                if (!anchor.getOfficeLocation().equals(candidate.getOfficeLocation()))
                    continue;

                cluster.add(requests.get(j));
                assigned[j] = true;
            }

            clusters.add(cluster);
        }

        return clusters;
    }

    /**
     * Haversine formula to compute distance in km between two lat/lon points.
     */
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Check if two employees' pickup windows overlap.
     * If either has no preference, assume overlap (they're flexible).
     */
    private boolean pickupWindowsOverlap(CommutePreference pref1, CommutePreference pref2) {
        if (pref1 == null || pref2 == null)
            return true;
        // Overlap exists if one's start is before the other's end AND vice versa
        return !pref1.getPickupStartTime().isAfter(pref2.getPickupEndTime())
                && !pref2.getPickupStartTime().isAfter(pref1.getPickupEndTime());
    }

    /**
     * Check gender constraint: if ANY member of the cluster (or the candidate)
     * requires same-gender rides, the candidate must match the cluster's gender.
     */
    private boolean genderConstraintSatisfied(List<RideRequest> cluster, Employee candidate,
            Map<Long, CommutePreference> preferenceMap) {
        // Check if candidate requires same gender
        CommutePreference candidatePref = preferenceMap.get(candidate.getId());
        boolean candidateRequires = candidatePref != null
                && Boolean.TRUE.equals(candidatePref.getSameGenderRequired());

        // Check if any cluster member requires same gender
        boolean clusterRequires = cluster.stream().anyMatch(req -> {
            CommutePreference pref = preferenceMap.get(req.getEmployee().getId());
            return pref != null && Boolean.TRUE.equals(pref.getSameGenderRequired());
        });

        if (!candidateRequires && !clusterRequires)
            return true;

        // If any requires same gender, all must be same gender
        String clusterGender = cluster.get(0).getEmployee().getGender();
        return clusterGender.equalsIgnoreCase(candidate.getGender());
    }

    /**
     * Compute total route distance as sum of consecutive Haversine distances.
     */
    private double computeRouteDistance(List<RideRequest> cluster) {
        if (cluster.size() <= 1)
            return 0.0;
        double total = 0.0;
        for (int i = 0; i < cluster.size() - 1; i++) {
            Employee e1 = cluster.get(i).getEmployee();
            Employee e2 = cluster.get(i + 1).getEmployee();
            total += haversineDistance(
                    e1.getHomeLatitude(), e1.getHomeLongitude(),
                    e2.getHomeLatitude(), e2.getHomeLongitude());
        }
        return total;
    }
}

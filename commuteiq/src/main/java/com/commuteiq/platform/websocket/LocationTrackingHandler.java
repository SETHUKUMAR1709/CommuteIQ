package com.commuteiq.platform.websocket;

import com.commuteiq.platform.entity.SafetyEventType;
import com.commuteiq.platform.service.SafetyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time driver location tracking.
 *
 * Drivers send JSON messages with format:
 * { "ridePlanId": 123, "latitude": 12.9716, "longitude": 77.5946 }
 *
 * The handler tracks the last known position for each ride plan and
 * checks for route deviation exceeding a configurable threshold.
 * If deviation is detected, a SafetyEvent of type ROUTE_DEVIATION is created.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationTrackingHandler extends TextWebSocketHandler {

    private final SafetyService safetyService;
    private final ObjectMapper objectMapper;

    @Value("${app.route.deviation-threshold-meters:500}")
    private double deviationThresholdMeters;

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    // Track last known positions per ride plan
    private final ConcurrentHashMap<Long, double[]> lastKnownPositions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode payload = objectMapper.readTree(message.getPayload());

            Long ridePlanId = payload.get("ridePlanId").asLong();
            double latitude = payload.get("latitude").asDouble();
            double longitude = payload.get("longitude").asDouble();

            log.debug("Location update for ridePlan {}: [{}, {}]", ridePlanId, latitude, longitude);

            // Check for route deviation against last known position
            double[] lastPosition = lastKnownPositions.get(ridePlanId);
            if (lastPosition != null) {
                double distance = haversineDistanceMeters(
                        lastPosition[0], lastPosition[1], latitude, longitude);

                if (distance > deviationThresholdMeters) {
                    log.warn("Route deviation detected for ridePlan {}: {} meters", ridePlanId, distance);

                    safetyService.recordSafetyEvent(
                            ridePlanId,
                            SafetyEventType.ROUTE_DEVIATION,
                            String.format("Route deviation of %.1f meters detected. " +
                                    "From [%.6f, %.6f] to [%.6f, %.6f]",
                                    distance, lastPosition[0], lastPosition[1], latitude, longitude));

                    // Send alert back to driver
                    session.sendMessage(new TextMessage(
                            objectMapper.writeValueAsString(
                                    java.util.Map.of(
                                            "alert", "ROUTE_DEVIATION",
                                            "message", "Route deviation detected: " + Math.round(distance) + "m",
                                            "ridePlanId", ridePlanId))));
                }
            }

            // Update last known position
            lastKnownPositions.put(ridePlanId, new double[] { latitude, longitude });

            // Acknowledge receipt
            session.sendMessage(new TextMessage(
                    objectMapper.writeValueAsString(
                            java.util.Map.of("status", "OK", "ridePlanId", ridePlanId))));

        } catch (Exception e) {
            log.error("Error processing location update: {}", e.getMessage());
            session.sendMessage(new TextMessage(
                    objectMapper.writeValueAsString(
                            java.util.Map.of("status", "ERROR", "message", e.getMessage()))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed: {} status: {}", session.getId(), status);
    }

    /**
     * Haversine formula returning distance in meters.
     */
    private double haversineDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}

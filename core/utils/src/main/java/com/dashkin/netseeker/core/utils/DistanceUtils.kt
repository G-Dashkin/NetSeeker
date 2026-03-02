package com.dashkin.netseeker.core.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utility functions for geographic coordinate calculations.
 */
object DistanceUtils {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the distance between two geographic coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of the first point in degrees.
     * @param lon1 Longitude of the first point in degrees.
     * @param lat2 Latitude of the second point in degrees.
     * @param lon2 Longitude of the second point in degrees.
     * @return Distance in kilometers.
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Formats distance for display: shows meters if under 1 km, otherwise kilometers.
     *
     * @param distanceKm Distance in kilometers.
     * @return Formatted string with appropriate unit.
     */
    fun formatDistance(distanceKm: Double): String {
        val distanceM = distanceKm * 1000
        return if (distanceM < 1000) {
            "${distanceM.toInt()} m"
        } else {
            String.format("%.1f km", distanceKm)
        }
    }
}

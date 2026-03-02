package com.dashkin.netseeker.feature.map.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.map.R
import com.dashkin.netseeker.feature.map.databinding.FragmentMapBinding
import com.dashkin.netseeker.feature.map.domain.model.SpotQuality
import com.dashkin.netseeker.feature.map.domain.model.WifiSpot
import com.dashkin.netseeker.feature.map.presentation.state.SpotFilter
import com.dashkin.netseeker.feature.map.presentation.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapFragment : BaseFragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    private val currentMarkers = mutableMapOf<String, Marker>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        setupMap()
        setupFilterChips()
        setupFab()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentByTag(TAG_MAP_FRAGMENT) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also { fragment ->
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapContainer, fragment, TAG_MAP_FRAGMENT)
                    .commitNow()
            }
        mapFragment.getMapAsync { map ->
            googleMap = map
            configureMap(map)
            observeFilteredSpots()
        }
    }

    private fun configureMap(map: GoogleMap) {
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(STUB_USER_LOCATION, DEFAULT_ZOOM))
    }

    private fun observeFilteredSpots() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredSpots.collect { spots ->
                    updateMarkers(spots)
                }
            }
        }
    }

    // Incrementally syncs map markers with [spots]:
    // - removes markers whose spots are no longer in the list
    // - adds markers for newly appeared spots
    // Avoids [GoogleMap.clear] + full re-add on every filter change,
    // which would cause flicker and poor performance with large datasets.
    private fun updateMarkers(spots: List<WifiSpot>) {
        val map = googleMap ?: return
        val incomingIds = spots.associateBy { it.id }

        // Remove markers that are no longer in the filtered list.
        val removedIds = currentMarkers.keys.filter { it !in incomingIds }
        removedIds.forEach { id ->
            currentMarkers.remove(id)?.remove()
        }

        // Add markers for spots not yet on the map.
        spots.forEach { spot ->
            if (spot.id !in currentMarkers) {
                val snippet = spot.downloadMbps
                    ?.let { "%.1f Mbps".format(it) }
                    ?: "No speed data"
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(spot.latitude, spot.longitude))
                        .title(spot.ssid)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(spot.quality.toMarkerHue()))
                )
                if (marker != null) currentMarkers[spot.id] = marker
            }
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipFast -> SpotFilter.FAST
                R.id.chipModerate -> SpotFilter.MODERATE
                R.id.chipSlow -> SpotFilter.SLOW
                else -> SpotFilter.ALL
            }
            viewModel.setFilter(filter)
        }
    }

    private fun setupFab() {
        binding.fabSpeedTest.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Connect to a WiFi network to run a Speed Test",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentMarkers.clear()
        googleMap = null
        _binding = null
    }

    companion object {
        private const val TAG_MAP_FRAGMENT = "google_map_fragment"
        private const val DEFAULT_ZOOM = 14f
        private val STUB_USER_LOCATION = LatLng(55.7560, 37.6175)
    }
}

private fun SpotQuality.toMarkerHue(): Float = when (this) {
    SpotQuality.FAST -> BitmapDescriptorFactory.HUE_GREEN
    SpotQuality.MODERATE -> BitmapDescriptorFactory.HUE_YELLOW
    SpotQuality.SLOW -> BitmapDescriptorFactory.HUE_RED
    SpotQuality.UNKNOWN -> BitmapDescriptorFactory.HUE_AZURE
}

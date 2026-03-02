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
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapFragment : BaseFragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null

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

    private fun updateMarkers(spots: List<WifiSpot>) {
        val map = googleMap ?: return
        map.clear()
        spots.forEach { spot ->
            val snippet = spot.downloadMbps
                ?.let { "%.1f Mbps".format(it) }
                ?: "No speed data"
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(spot.latitude, spot.longitude))
                    .title(spot.ssid)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(spot.quality.toMarkerHue()))
            )
        }
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.setFilter(SpotFilter.ALL)
        }
        binding.chipFast.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.setFilter(SpotFilter.FAST)
        }
        binding.chipModerate.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.setFilter(SpotFilter.MODERATE)
        }
        binding.chipSlow.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.setFilter(SpotFilter.SLOW)
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

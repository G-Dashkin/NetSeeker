package com.dashkin.netseeker.feature.speedtest.presentation.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.core.wifi.WifiConnectionInfo
import com.dashkin.netseeker.feature.speedtest.R
import com.dashkin.netseeker.feature.speedtest.databinding.FragmentSpeedtestBinding
import com.dashkin.netseeker.feature.speedtest.presentation.state.SpeedTestState
import com.dashkin.netseeker.feature.speedtest.presentation.state.TestPhase
import com.dashkin.netseeker.feature.speedtest.presentation.viewmodel.SpeedTestViewModel
import kotlinx.coroutines.launch

class SpeedTestFragment : BaseFragment(R.layout.fragment_speedtest) {

    private var _binding: FragmentSpeedtestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SpeedTestViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Refresh is handled by the WifiObserver flow on next network event. */ }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSpeedtestBinding.bind(view)
        requestLocationPermissionIfNeeded()
        setupButton()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLocationPermissionIfNeeded() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(permission)
        }
    }

    private fun setupButton() {
        binding.btnStartTest.setOnClickListener {
            if (viewModel.state.value.isRunning) viewModel.cancelTest() else viewModel.startTest()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: SpeedTestState) {
        renderWifiCard(state.wifiInfo, state.isWifiConnected)
        renderGauge(state)
        renderProgress(state)
        renderResults(state)
        renderButton(state)
        renderError(state.error)
    }

    private fun renderWifiCard(info: WifiConnectionInfo?, connected: Boolean) {
        binding.wifiCard.strokeColor = ContextCompat.getColor(
            requireContext(),
            if (connected) R.color.speedtest_wifi_connected_stroke
            else R.color.speedtest_wifi_disconnected_stroke,
        )

        if (info != null) {
            binding.tvWifiStatus.text = getString(R.string.speedtest_wifi_connected, info.ssid)
            binding.tvWifiBand.text = getString(
                R.string.speedtest_wifi_band_speed,
                info.band,
                info.linkSpeedMbps,
            )
        } else {
            binding.tvWifiStatus.text = getString(R.string.speedtest_wifi_disconnected)
            binding.tvWifiBand.text = getString(R.string.speedtest_wifi_connect_hint)
        }
    }

    private fun renderGauge(state: SpeedTestState) {
        binding.speedGauge.setSpeed(state.currentSpeedMbps, animate = state.isRunning)

        binding.tvPhaseLabel.text = when (state.phase) {
            TestPhase.IDLE -> if (state.isWifiConnected) getString(R.string.speedtest_phase_ready)
            else getString(R.string.speedtest_phase_no_wifi)

            TestPhase.PINGING -> getString(R.string.speedtest_phase_pinging)
            TestPhase.DOWNLOADING -> getString(R.string.speedtest_phase_downloading, state.progress)
            TestPhase.UPLOADING -> getString(R.string.speedtest_phase_uploading, state.progress)
            TestPhase.COMPLETED -> getString(R.string.speedtest_phase_completed)
            TestPhase.ERROR -> getString(R.string.speedtest_phase_error)
        }
    }

    private fun renderProgress(state: SpeedTestState) {
        binding.progressBar.isVisible = state.isRunning
        if (state.isRunning) binding.progressBar.progress = state.progress
    }

    private fun renderResults(state: SpeedTestState) {
        val result = state.result
        val showResults = state.phase == TestPhase.COMPLETED && result != null
        binding.resultsLayout.isVisible = showResults

        if (result != null) {
            binding.tvDownloadSpeed.text = formatSpeed(result.downloadMbps)
            binding.tvUploadSpeed.text = formatSpeed(result.uploadMbps)
            binding.tvPing.text = result.pingMs.toString()
        }
    }

    private fun renderButton(state: SpeedTestState) {
        binding.btnStartTest.isEnabled = state.isWifiConnected || state.isRunning
        binding.btnStartTest.text = if (state.isRunning) getString(R.string.speedtest_btn_cancel)
        else getString(R.string.speedtest_btn_start)
    }

    private fun renderError(error: String?) {
        binding.tvError.isVisible = error != null
        binding.tvError.text = error
    }

    private fun formatSpeed(mbps: Float): String = if (mbps < 10f) "%.1f".format(mbps) else "%.0f".format(mbps)
}

package com.dashkin.netseeker.feature.settings.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.settings.R
import com.dashkin.netseeker.feature.settings.data.repository.SettingsRepositoryImpl
import com.dashkin.netseeker.feature.settings.databinding.FragmentSettingsBinding
import com.dashkin.netseeker.feature.settings.domain.model.SpeedUnit
import com.dashkin.netseeker.feature.settings.presentation.state.SettingsState
import com.dashkin.netseeker.feature.settings.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    // Guards against feedback loops when sliders/toggles are updated programmatically.
    private var isUpdatingUi = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        viewModel = createViewModel()
        setupThresholdSliders()
        setupSearchRadiusSlider()
        setupSpeedUnitToggle()
        observeState()
    }

    private fun createViewModel(): SettingsViewModel {
        val prefs = requireContext().getSharedPreferences(
            SettingsRepositoryImpl.PREFS_NAME,
            Context.MODE_PRIVATE,
        )
        val repository = SettingsRepositoryImpl(prefs)
        return ViewModelProvider(this, SettingsViewModel.factory(repository))[SettingsViewModel::class.java]
    }

    private fun setupThresholdSliders() {
        binding.sliderFastThreshold.addOnChangeListener { _, value, fromUser ->
            if (fromUser && !isUpdatingUi) viewModel.onFastThresholdChanged(value)
        }
        binding.sliderSlowThreshold.addOnChangeListener { _, value, fromUser ->
            if (fromUser && !isUpdatingUi) viewModel.onSlowThresholdChanged(value)
        }
    }

    private fun setupSearchRadiusSlider() {
        binding.sliderSearchRadius.addOnChangeListener { _, value, fromUser ->
            if (fromUser && !isUpdatingUi) viewModel.onSearchRadiusChanged(value.toInt())
        }
    }

    private fun setupSpeedUnitToggle() {
        binding.toggleSpeedUnit.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked || isUpdatingUi) return@addOnButtonCheckedListener
            val unit = if (checkedId == R.id.buttonMbps) SpeedUnit.MBPS else SpeedUnit.MBYTES_PER_SECOND
            viewModel.onSpeedUnitChanged(unit)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> renderState(state) }
            }
        }
    }

    private fun renderState(state: SettingsState) {
        isUpdatingUi = true
        binding.sliderFastThreshold.value = state.fastThresholdMbps
        binding.sliderSlowThreshold.value = state.slowThresholdMbps
        binding.sliderSearchRadius.value = state.searchRadiusMeters.toFloat()
        binding.labelFastThresholdValue.text =
            getString(R.string.settings_mbps_value, state.fastThresholdMbps.toInt())
        binding.labelSlowThresholdValue.text =
            getString(R.string.settings_mbps_value, state.slowThresholdMbps.toInt())
        binding.labelSearchRadiusValue.text =
            getString(R.string.settings_radius_value, state.searchRadiusMeters)
        binding.toggleSpeedUnit.check(
            if (state.speedUnit == SpeedUnit.MBPS) R.id.buttonMbps else R.id.buttonMBps
        )
        isUpdatingUi = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

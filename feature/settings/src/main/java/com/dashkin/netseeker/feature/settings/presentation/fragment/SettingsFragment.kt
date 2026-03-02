package com.dashkin.netseeker.feature.settings.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.settings.R
import com.dashkin.netseeker.feature.settings.presentation.viewmodel.SettingsViewModel

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

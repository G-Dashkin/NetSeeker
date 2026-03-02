package com.dashkin.netseeker.feature.speedtest.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.speedtest.R
import com.dashkin.netseeker.feature.speedtest.presentation.viewmodel.SpeedTestViewModel

class SpeedTestFragment : BaseFragment(R.layout.fragment_speedtest) {

    private val viewModel: SpeedTestViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

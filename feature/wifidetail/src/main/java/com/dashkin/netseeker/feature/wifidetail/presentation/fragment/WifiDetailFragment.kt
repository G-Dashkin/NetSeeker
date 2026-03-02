package com.dashkin.netseeker.feature.wifidetail.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.wifidetail.R
import com.dashkin.netseeker.feature.wifidetail.presentation.viewmodel.WifiDetailViewModel

class WifiDetailFragment : BaseFragment(R.layout.fragment_wifi_detail) {

    private val viewModel: WifiDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

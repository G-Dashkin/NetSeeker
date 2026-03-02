package com.dashkin.netseeker.feature.map.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.map.R
import com.dashkin.netseeker.feature.map.presentation.viewmodel.MapViewModel

class MapFragment : BaseFragment(R.layout.fragment_map) {

    private val viewModel: MapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

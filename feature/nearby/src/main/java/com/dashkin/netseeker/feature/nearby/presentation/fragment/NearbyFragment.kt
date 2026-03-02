package com.dashkin.netseeker.feature.nearby.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.dashkin.netseeker.core.ui.BaseFragment
import com.dashkin.netseeker.feature.nearby.R
import com.dashkin.netseeker.feature.nearby.presentation.viewmodel.NearbyViewModel

class NearbyFragment : BaseFragment(R.layout.fragment_nearby) {

    private val viewModel: NearbyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

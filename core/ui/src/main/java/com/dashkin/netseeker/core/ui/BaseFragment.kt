package com.dashkin.netseeker.core.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Base class for all fragments in the application.
 * Provides common infrastructure for error handling and loading states.
 */
abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId)

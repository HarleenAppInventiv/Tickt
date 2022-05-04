package com.example.ticktapp.mvvm.view.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentOnboardingBinding

class OnboardingFragment : BaseFragment() {
    private var position: Int = 0
    private lateinit var bindingView:FragmentOnboardingBinding
    private lateinit var mRootView: View

    companion object {
        fun getInstance(position: Int): OnboardingFragment {
            val simpleTutorialFragment = OnboardingFragment()
            val bundle = Bundle()
            bundle.putInt(IntentConstants.KEY_ONBOARDING_POSITION, position)
            simpleTutorialFragment.arguments = bundle
            return simpleTutorialFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingView = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding, container, false)
        mRootView = bindingView.root
        return mRootView
    }


    override fun initialiseFragmentBaseViewModel() {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundleData()
        setUpUi()
    }

    private fun getBundleData() {
        arguments?.let {
            position = it.getInt(IntentConstants.KEY_ONBOARDING_POSITION)
        }
    }

    private fun setUpUi() {
        var imageDrawableId = 0
        when (position) {
            1 -> {
                imageDrawableId = R.drawable.onboarding_one
            }
            2 -> {
                imageDrawableId = R.drawable.onboarding_two
            }
            3 -> {
                imageDrawableId = R.drawable.onboarding_three
            }
        }
        bindingView.ivTutorial.setImageResource(imageDrawableId)
    }

}
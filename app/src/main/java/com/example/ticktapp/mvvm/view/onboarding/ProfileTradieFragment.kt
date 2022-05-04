package com.example.ticktapp.mvvm.view.onboarding

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.databinding.FragmentProfileBinding
import com.example.ticktapp.mvvm.view.WelcomeActivity
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import org.json.JSONObject




class ProfileTradieFragment : BaseFragment() {
    private lateinit var mBinding: FragmentProfileBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(BaseViewModel::class.java) }

    companion object {
        fun getInstance(param: String): ProfileTradieFragment {
            val fragment = ProfileTradieFragment()
            val data = Bundle()
            data.putSerializable("data", param)
            fragment.arguments = data
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        val data = arguments?.getString("data")
        if (data.equals("1")) {
            mBinding.tvLogout.visibility = View.VISIBLE
        }
        setObservers()
        setListener()
    }

    private fun setObservers() {
        setFragmentBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun setListener() {
        mBinding.tvLogout.setOnClickListener {
            logOut()
        }
    }

    fun logOut() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_you_want_logout))
                .setCancelable(false)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    mViewModel.doLogout()
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                .show()
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.LOGOUT -> {
                clearNotifications()
                logoutUserOnMoEngage()
                logoutMixPanel()
                PreferenceManager.clearAllPrefs()
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finishAffinity()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun clearNotifications(){
        val notificationManager: NotificationManager =
            requireActivity().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun logoutUserOnMoEngage() {
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.SUCCESS_STATUS, true)
        signUpProperty.addAttribute(MoEngageConstants.CURRENT_PAGE, "Profile")

        MoEngageUtils.sendEvent(
            requireActivity(),
            MoEngageConstants.MOENGAGE_EVENT_LOG_OUT,
            signUpProperty
        )

        //Logout from Mo-Enagage SDK
        MoEHelper.getInstance(requireActivity()).logoutUser()
    }

    private fun logoutMixPanel(){
        val mixpanel = MixpanelAPI.getInstance(
            requireContext(),
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.SUCCESS_STATUS, true)
        props.put(MoEngageConstants.CURRENT_PAGE, "Profile")

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_LOG_OUT, props)
    }

}
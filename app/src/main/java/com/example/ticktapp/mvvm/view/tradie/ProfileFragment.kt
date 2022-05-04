package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.databinding.FragmentProfileBinding
import com.example.ticktapp.mvvm.view.WelcomeActivity

class ProfileFragment : BaseFragment() {
    private lateinit var mBinding: FragmentProfileBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(BaseViewModel::class.java) }

    companion object {
        fun getInstance(param: String): ProfileFragment {
            val fragment = ProfileFragment()
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
            mBinding.tvVersions.visibility = View.VISIBLE
            val pinfo: PackageInfo? =
                activity?.packageName?.let { activity?.packageManager?.getPackageInfo(it, 0) }
            mBinding.tvVersions.text = getString(R.string.version_) + " " + pinfo?.versionName
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
                val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                PreferenceManager.clearAllPrefs()
                PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finish()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

}
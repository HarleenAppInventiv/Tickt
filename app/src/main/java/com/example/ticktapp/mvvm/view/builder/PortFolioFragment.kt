package com.example.ticktapp.mvvm.view.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentPortfolioBinding

class PortFolioFragment : BaseFragment() {
    private lateinit var mBinding: FragmentPortfolioBinding
    private lateinit var mRootView: View

    companion object {
        fun getInstance(param: String, pos: Int): PortFolioFragment {
            val fragment = PortFolioFragment()
            val data = Bundle()
            data.putString("data", param)
            data.putInt("pos", pos)
            fragment.arguments = data
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_portfolio, container, false)
        mRootView = mBinding.root
        listener()
        return mRootView
    }

    fun listener() {
        mBinding.portfolioIv.setOnClickListener {
            if (activity is PortFolioActivity) {
                arguments?.getInt("pos", 0)
                    ?.let { (activity as PortFolioActivity).showFullScreenAlbum(it) }
            }
        }
    }

    override fun initialiseFragmentBaseViewModel() {
        val data = arguments?.getString("data")
        Glide.with(mBinding.root.context).load(data)
            .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
            .into(mBinding.portfolioIv)
    }


}
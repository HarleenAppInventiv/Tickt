package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAddBuilderReviewCompletedBinding
import com.example.ticktapp.databinding.ActivityRateBuilderCommentBinding

class AddBuilderReviewActivityCompleted : BaseActivity() {
    private lateinit var mBinding: ActivityAddBuilderReviewCompletedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_builder_review_completed)
        mBinding.tvYellowBtn.setOnClickListener{
            setResult(Activity.RESULT_OK)
            finish()
        }

    }
}
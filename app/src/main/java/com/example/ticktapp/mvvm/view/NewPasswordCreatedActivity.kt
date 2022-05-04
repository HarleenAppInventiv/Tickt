package com.example.ticktapp.mvvm.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityDoneBinding
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity

class NewPasswordCreatedActivity : BaseActivity() {
    lateinit var mBinding: ActivityDoneBinding
    private var from: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        from = intent.getIntExtra(IntentConstants.FROM, 0)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_password_created)
        if (from == Constants.FORGOT_PASSWORD) {
            mBinding.tvHeading.setText(R.string.nice)
            mBinding.tvSubheading.setText(getString(R.string.You_have_updated_the_password))
            mBinding.tvYellowBtn.setText(getString(R.string.log_in))
            mBinding.rlBackground.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_251)
        }


        mBinding.tvYellowBtn.setOnClickListener {
            if (from == Constants.FORGOT_PASSWORD) {
                finish()
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            } else {
                PreferenceManager.putBoolean(PreferenceManager.IS_LOGIN, true)
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                if (!userType.isNullOrEmpty() && userType.equals("1")) {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                }
                }
                ActivityCompat.finishAffinity(this)
            }
        }
    }

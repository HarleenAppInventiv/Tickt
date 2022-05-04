package com.example.ticktapp.mvvm.view.tradie

import android.os.Bundle
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_verification_process.*

class VerificationProcessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_process)
        iv_back.setOnClickListener {
            finish()
        }
    }
}
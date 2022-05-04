package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.tradie.PortFolio
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.exampl.AddEditPortfolioActivity
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ViewPagerAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPortfolioBinding
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity
import com.example.ticktapp.mvvm.viewmodel.PortfolioViewModel

class PortFolioActivity : BaseActivity(), View.OnClickListener {
    private var isEditable: Boolean = false
    private lateinit var dataPortfolio: PortFolio
    private lateinit var mBinding: ActivityPortfolioBinding
    private lateinit var adapter: ViewPagerAdapter
    private val mPortfolioViewModel by lazy { ViewModelProvider(this).get(PortfolioViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_portfolio)
        setUpListeners()
        setStatusBarColor()
        setObservers()
        setLightStatusBar(mBinding.root)
        getIntentData()
        initRecyclerView()
    }

    private fun setObservers() {
        setBaseViewModel(mPortfolioViewModel)
        mPortfolioViewModel.getResponseObserver().observe(this, this)
    }

    private fun setUpListeners() {
        mBinding.portfolioIvBack.setOnClickListener { onBackPressed() }
        mBinding.ivSetting.setOnClickListener(this)
    }

    private fun getIntentData() {
        dataPortfolio = intent.getSerializableExtra("data") as PortFolio
        if (dataPortfolio.isEditable)
            mBinding.ivSetting.visibility = View.VISIBLE
    }

    fun showFullScreenAlbum(pos: Int) {
        startActivity(
            Intent(this, DialogImageViewPostActivity::class.java)
                .putExtra("photos", dataPortfolio.portfolioImage as ArrayList<String>)
                .putExtra("pos", pos)
        )
    }

    private fun initRecyclerView() {
        adapter = ViewPagerAdapter(supportFragmentManager)
        var pos = 0
        dataPortfolio.portfolioImage?.forEach {
            adapter.addFragment(PortFolioFragment.getInstance(it, pos), "")
            pos++
        }
        mBinding.tvTitle.text = dataPortfolio.jobName
        mBinding.vpImages.adapter = adapter
        if (dataPortfolio.jobDescription?.length == 0)
            mBinding.tvDesc.text = "-"
        else
            mBinding.tvDesc.text = dataPortfolio.jobDescription
        mBinding.vpImages.offscreenPageLimit = dataPortfolio.portfolioImage?.size!!
        mBinding.circleIndicatorPager.setViewPager(mBinding.vpImages)
        if (adapter.count == 0 || adapter.count == 1) {
            mBinding.circleIndicatorPager.visibility = View.GONE
        } else
            mBinding.circleIndicatorPager.visibility = View.VISIBLE
    }


    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.ivSetting -> {
                showMenu(mBinding.ivSetting)
            }
        }
    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_bank_details_edit, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.text = view.context.getString(R.string.edit)
        tvOptionTwo.text = view.context.getString(R.string.delete)
        tvOptionOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit, 0, 0, 0)
        tvOptionOne.setOnClickListener {
            popup.dismiss()
            val intents = Intent(this, AddEditPortfolioActivity::class.java)
            intents.putExtra("jobName", dataPortfolio.jobName)
            intents.putExtra("jobDescription", dataPortfolio.jobDescription)
            intents.putExtra("portfolioId", dataPortfolio.portfolioId)
            intents.putExtra("photos", dataPortfolio.portfolioImage as ArrayList<String>)
            intents.putExtra("isReturn", true)
            intents.putExtra("isBuilder", intent.getBooleanExtra("isBuilder", false))
            resultLauncher.launch(intents)

        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
            title.text = getString(R.string.delete)

            val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
            msg.text = getString(R.string.are_you_sure_you_want_to_delete_portfolio)

            val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
            dialogBtn_okay.text = getString(R.string.ok)
            dialogBtn_okay.setOnClickListener {
                dialog.dismiss()
                mPortfolioViewModel.deletePortfolio(dataPortfolio.portfolioId!!)
            }

            val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
            dialogBtn_cancel.text = getString(R.string.cancel)
            dialogBtn_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        popup.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popup.dismiss()
                return@setTouchInterceptor true
            }
            false
        }
        popup.animationStyle = R.style.popupWindowAnim
        popup.showAtLocation(layout, Gravity.TOP or Gravity.END, 50, 220)

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.DELETE_PORTFOLIO -> {
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.DELETE_PORTFOLIO -> {
                val intent = Intent()
                intent.putExtra("isDeleted", true)
                intent.putExtra("portfolioId", dataPortfolio.portfolioId!!)
                setResult(RESULT_OK, intent)
                finish()

            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val jobName = data.getStringExtra("jobName")
                    val jobDesc = data.getStringExtra("jobDescription")
                    val portfolioUrl = data.getStringArrayListExtra("url")
                    val portfolioId = data.getStringExtra("portfolioId")
                    dataPortfolio.jobName = jobName
                    dataPortfolio.jobDescription = jobDesc
                    dataPortfolio.portfolioId = portfolioId
                    dataPortfolio.portfolioImage = portfolioUrl
                    initRecyclerView()
                    isEditable = true
                }

            }
        }

    override fun onBackPressed() {
        if (!isEditable)
            super.onBackPressed()
        else {
            val intent = Intent()
            intent.putExtra("jobName", dataPortfolio.jobName)
            intent.putExtra("jobDescription", dataPortfolio.jobDescription)
            intent.putExtra("url", dataPortfolio.portfolioImage as ArrayList<String>)
            intent.putExtra("portfolioId", dataPortfolio.portfolioId)
            intent.putExtra("isDeleted", false)
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}

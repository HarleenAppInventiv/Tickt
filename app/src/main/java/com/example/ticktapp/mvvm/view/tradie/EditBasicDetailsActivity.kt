package com.example.ticktapp.mvvm.view.tradie

import CoreUtils
import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ShowUploadedDocumentsAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.LoaderType
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityEditBasicProfileBinding
import com.app.core.model.jobmodel.QualifiedDoc
import com.example.ticktapp.mvvm.view.AddDocumentActivity
import com.example.ticktapp.mvvm.view.FileOpenActivity
import com.example.ticktapp.mvvm.viewmodel.TradieProfileViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileDownloadManager


class EditBasicDetailsActivity : BaseActivity(), View.OnClickListener,
    ShowUploadedDocumentsAdapter.DocListAdapterListener, PermissionHelper.IGetPermissionListener {

    private var url: String? = null
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var mBinding: ActivityEditBasicProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieProfileViewModel::class.java) }
    private lateinit var mAdapter: ShowUploadedDocumentsAdapter
    private var qualList = ArrayList<QualifiedDoc>()

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_basic_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setObservers()
        initRecyclerView()
        mViewModel.getTradieBasicProfileDetails()
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        val socialId = PreferenceManager.getString(PreferenceManager.SOCIAL_ID)
        if (!socialId.isNullOrEmpty())
            mBinding.tvChange.visibility = View.GONE
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun initRecyclerView() {
        mAdapter = ShowUploadedDocumentsAdapter(qualList, this)
        mBinding.rvDocument.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        mBinding.rvDocument.layoutManager = layoutManager
    }


    private fun listener() {
        mBinding.jobDescBack.setOnClickListener(this)
        mBinding.tvChangePass.setOnClickListener(this)
        mBinding.tvChange.setOnClickListener(this)
        mBinding.tvAddDoc.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.edtName.addTextChangedListener(GenricWatcher(mBinding.edtName))
        mBinding.edtEmail.addTextChangedListener(GenricWatcher(mBinding.edtEmail))
        mBinding.edtAbn.addTextChangedListener(GenricWatcher(mBinding.edtAbn))

    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TRADIE_BASIC_PROFILE -> {
                mViewModel.mTradieInitalProfileData.let {
                    mBinding.model = mViewModel.mTradieInitalProfileData
                    mBinding.edtName.setText(mViewModel.mTradieInitalProfileData.fullName)
                    if (mViewModel.mTradieInitalProfileData.businessName != null) {
                        mBinding.edtCompanyName.setText(mViewModel.mTradieInitalProfileData.businessName)
                    }
                    mBinding.edtName.setSelection(mBinding.edtName.text.toString().length)
                    mViewModel.mTradieInitalProfileData.qualificationDoc?.let {
                        if (mViewModel.mTradieInitalProfileData.qualificationDoc!!.size > 0) {
                            mBinding.tvQualificationDoc.visibility = View.VISIBLE
                            mBinding.rvDocument.visibility = View.VISIBLE
                            qualList.clear()
                            qualList.addAll(mViewModel.mTradieInitalProfileData.qualificationDoc!!)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }

            }
            ApiCodes.TRADIE_EDIT_PROFILE -> {
                val intent = Intent()
                intent.putExtra("name", mBinding.edtName.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.jobDescBack -> onBackPressed()
            mBinding.tvChangePass -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
            mBinding.tvAddDoc -> {
                startActivityForResult(
                    Intent(
                        this,
                        AddDocumentActivity::class.java
                    ).putExtra("isReturn", true), PermissionConstants.UPLOAD_DOC
                )
            }
            mBinding.tvChange -> {
                startActivity(
                    Intent(this, ChangeEmailActivity::class.java)
                        .putExtra("email", mBinding.edtEmail.text.toString())
                )
            }
            mBinding.tvSave -> {
                if (isValid()) {
                    val param = HashMap<String, Any>()
                    param.put("fullName", mBinding.edtName.text.toString())
                    param.put("mobileNumber", mBinding.edtPhoneNum.text.toString())
                    param.put("email", mBinding.edtEmail.text.toString())
                    param.put("abn", mBinding.edtAbn.text.toString())
                    param.put("businessName", mBinding.edtCompanyName.text.toString())
                    param.put("qualificationDoc", addQualificationData())
                    mViewModel.tradieEditBasicProfile(param)
                }
            }
        }
    }

    private fun isValid(): Boolean {
        mBinding.tvNameError.visibility = View.INVISIBLE
        mBinding.tvEmailError.visibility = View.INVISIBLE
        mBinding.tvAbnError.visibility = View.INVISIBLE

        if (mBinding.edtName.text.toString().trim().isEmpty()) {
            mBinding.tvNameError.visibility = View.VISIBLE
            mBinding.tvNameError.setText(getString(R.string.please_enter_full_name))
            return false
        } else if (mBinding.edtEmail.text.toString().trim().isEmpty()) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.please_enter_email_address))
            return false

        } else if (!CoreUtils.isEmailValid(mBinding.edtEmail.text.toString())) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.email_is_not_valid))
            return false

        } else if (mBinding.edtAbn.text.toString().trim().isEmpty()) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.please_enter_abn))
            return false

        } else if (mBinding.edtAbn.text.toString().replace(" ", "").length != 11) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.abn_invalid_11))
            return false

        } else if (!CoreUtils.validABN(
                mBinding.edtAbn.text.toString() ?: " "
            )
        ) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.abn_invalid))
            return false

        }
        return true
    }

    override fun onDocDelte(position: Int) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete_document),
            {
                qualList.removeAt(position)
                mAdapter.notifyItemRemoved(position)
                if (qualList.size == 0) {
                    mBinding.tvQualificationDoc.visibility = View.GONE
                    mBinding.rvDocument.visibility = View.GONE
                }
            },
            {
            },
            true
        )


    }

    override fun onDocClick(position: Int) {
        url = qualList.get(position).url
        if (url == null)
            return
        val extn =
            url?.substring(url!!.lastIndexOf('.') + 1, url!!.length)

        if (!extn.equals("pdf") && !extn.equals("doc") && !extn.equals("docx")) {
            startActivity(
                Intent(this, DocumentViewrActivity::class.java).putExtra(
                    "url",
                    qualList.get(position).url
                )
            )
            return
        }
        startActivity(
            Intent(this, FileOpenActivity::class.java).putExtra(
                "data",
                qualList.get(position).url
            )
        )
        return
        if (permissionHelper.hasPermission(
                this,
                permissions, PermissionConstants.PERMISSION
            )
        ) {
            openRequestedFile()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PermissionConstants.UPLOAD_DOC) {
            data?.let {
                val dataList = data.getSerializableExtra("doc") as ArrayList<QualifiedDoc>
                if (qualList.size > 0) {
                    val qualiDataIterator = qualList.iterator()
                    while (qualiDataIterator.hasNext()) {
                        val qualifyData = qualiDataIterator.next()
                        for (item1 in dataList) {
                            if (item1.qualification_id!!.equals(qualifyData.qualification_id)) {
                                qualiDataIterator.remove()
                            }
                        }
                    }


                    for (item in qualList) {
                        for (item1 in dataList) {
                            if (item1.qualification_id!!.equals(item.qualification_id)) {
                                qualList.remove(item)
                            }
                        }
                    }
                    qualList.addAll(dataList)
                } else {
                    qualList.addAll(dataList)
                    mBinding.tvQualificationDoc.visibility = View.VISIBLE
                    mBinding.rvDocument.visibility = View.VISIBLE
                }
                mAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun addQualificationData(): ArrayList<com.app.core.model.registrationmodel.Qualification> {
        var qualification: com.app.core.model.registrationmodel.Qualification
        val selectedQualification = ArrayList<com.app.core.model.registrationmodel.Qualification>()

        try {
            for (item in qualList) {
                qualification = com.app.core.model.registrationmodel.Qualification(
                    qualification_id = item.qualification_id,
                    url = item.url
                )
                selectedQualification.add(qualification)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return selectedQualification
    }

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view) {
                mBinding.edtAbn -> {
                    val origin: String = s.toString().replace(" ", "")
                    val formatStr: String = formatStrWithSpaces(origin)
                    if (!s.toString().equals(formatStr)) {
                        editTextSetContentMemorizeSelection(mBinding.edtAbn, formatStr)
                        if (before == 0 && count == 1 && formatStr[mBinding.edtAbn.selectionEnd - 1] == ' ') {
                            mBinding.edtAbn.setSelection(mBinding.edtAbn.selectionEnd + 1)
                        }
                    }
                }
            }
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view) {
                mBinding.edtName -> {
                    mBinding.tvNameError.visibility = View.INVISIBLE
                }
                mBinding.edtEmail -> {
                    mBinding.tvEmailError.visibility = View.INVISIBLE
                }
                mBinding.edtAbn -> {
                    mBinding.tvAbnError.visibility = View.INVISIBLE
                }

            }
        }

    }

    private fun formatStrWithSpaces(can: CharSequence): String {
        val sb = StringBuffer()
        for (i in can.indices) {
            if (i != 0 && (i == 2 || i == 5 || i == 8)) {
                sb.append(' ')
            }
            sb.append(can[i])
        }
        return sb.toString()
    }

    private fun editTextSetContentMemorizeSelection(
        editText: EditText,
        charSequence: CharSequence
    ) {
        var selectionStart = editText.selectionStart
        var selectionEnd = editText.selectionEnd
        editText.setText(charSequence.toString())
        if (selectionStart > charSequence.toString().length) {
            selectionStart = charSequence.toString().length
        }
        if (selectionStart < 0) {
            selectionStart = 0
        }
        if (selectionEnd > charSequence.toString().length) {
            selectionEnd = charSequence.toString().length
        }
        if (selectionEnd < 0) {
            selectionEnd = 0
        }
        editText.setSelection(selectionStart, selectionEnd)
    }

    override fun permissionGiven(requestCode: Int) {
        openRequestedFile()
    }

    override fun permissionCancel(requestCode: Int) {

    }

    private fun openRequestedFile() {
        val fileName =
            url?.substring(url!!.lastIndexOf('/') + 1, url!!.length)
        showProgressDialog(
            LoaderType.NORMAL,
            ""
        )
        FileDownloadManager.downloadFileFromServer(this,
            "file",
            url!!,
            fileName,
            { tag, response, file ->
                Log.e("Success", response)
                hideProgressDialog(
                    LoaderType.NORMAL,
                    ""
                )
                FileDownloadManager.openFile(this, file)
            },
            { tag, errorMsg ->
                showToastShort("Something went wrong with this file..try again later")
                hideProgressDialog(
                    LoaderType.NORMAL,
                    ""
                )
            })
    }
}
package com.example.ticktapp.dialog.dropBox

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.app.core.model.DropBoxData
import com.app.core.util.MediaType
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.example.ticktapp.R
import com.example.ticktapp.adapters.DropBoxItemAdapter
import com.example.ticktapp.databinding.BottomDropboxDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.io.File
import java.io.OutputStream


/**
 * dialog to show dropbox data and select
 *
 * @property IDropBoxListener callback for observing events in dialog
 *
 * @param context
 */
class DropBoxDialog(
    context: Context,
    private var accessToken: String,
    var listener: IDropBoxListener,
    var hideVideos: Boolean = false
) :
    DialogFragment(), View.OnClickListener, DropBoxItemAdapter.IDropBoxItemListener {
    private var job: Job? = null
    private val list = ArrayList<DropBoxData>()
    lateinit var mBinding: BottomDropboxDialogBinding
    private var adapter: DropBoxItemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.bottom_dropbox_dialog,
            null,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.decorView?.setBackgroundColor(
            ContextCompat.getColor(
                activity!!,
                android.R.color.transparent
            )
        )
        initUi()
    }

    private fun initUi() {
        mBinding.pbLoading.visibility = View.VISIBLE
        mBinding.rvDropBox.visibility = View.GONE

        mBinding.ivClose.setOnClickListener(this)
        adapter = DropBoxItemAdapter(list, this)
        mBinding.rvDropBox.adapter = adapter
        getDropBoxFiles()
    }

    private fun getDropBoxFiles() {
        val config = DbxRequestConfig.newBuilder("dropbox/Tickt-Test").build()
        val client = DbxClientV2(config, accessToken)

        job = lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                getData(client, "")
                activity?.runOnUiThread {
                    mBinding.pbLoading.visibility = View.GONE
                    mBinding.rvDropBox.visibility = View.VISIBLE
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getData(client: DbxClientV2, filePath: String) {
        var result = client.files().listFolder(filePath)
        while (true) {
            for (metadata in result.entries) {
                val data = DropBoxData(
                    metadata.pathDisplay,
                    metadata.name,
                    metadata.parentSharedFolderId,
                    metadata.pathLower,
                    multiStri = metadata.toStringMultiline()
                )
                val metaObj = JSONObject(metadata.toStringMultiline())
                if (metaObj.getString(".tag").equals("file")) {

                    val isValidFile = getFileType(metadata.name.toLowerCase()) {
                        data.fileType = it ?: 0
                    }

                    if (isValidFile) {
                        val dir = File(activity!!.filesDir, "/Tickt")
                        dir.mkdir()
                        val file = File(dir, metadata.name)
                        val outputStream: OutputStream = FileOutputStream(file)

                        client.files().download(metadata.pathLower, metaObj.getString("rev"))
                            .download(outputStream)
                        data.path = file.path
                        list.add(data)
                        Log.e("DROPBOX", "$data")
                    }
                } else {
                    Log.e("DROPBOX", "${metadata.pathDisplay}")

                    getData(client, metadata.pathDisplay)
                }


            }
            if (!result.hasMore) {
                break
            }

            result = client.files().listFolderContinue(result.cursor)
        }
    }


    fun getFileType(path: String, onResult: (fileType: Int?) -> Unit): Boolean {


        when {
            path.endsWith(".pdf") -> {
                onResult(MediaType.PDF)
            }
            path.endsWith(".mp4") || path.endsWith(".mov") || path.endsWith(".m4p") || path.endsWith(
                ".m4v"
            ) -> {
                if (hideVideos) {
                    return false
                } else {
                    onResult(MediaType.VIDEO)
                }
            }
            path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") -> {
                onResult(MediaType.IMAGE)
            }
            path.endsWith(".doc") || path.endsWith(".docx")  -> {
                onResult(MediaType.DOC)
            }
            else -> {
                return false
            }
        }

        return true


    }


    /**
     * Callback that types of events emit by the interface
     *
     */
    interface IDropBoxListener {
        fun onFileSelected(path: String?)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_close -> {
                job?.cancel()
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onItemClick(position: Int, data: DropBoxData) {
        listener.onFileSelected(data.path)
        dismiss()
    }


}
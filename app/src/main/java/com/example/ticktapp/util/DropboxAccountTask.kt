package com.example.ticktapp.util

import android.os.AsyncTask
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import java.lang.Exception
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig


class DropboxAccountTask : AsyncTask<Void, Void, FullAccount>() {

    private var dbxClient: DbxClientV2? = null
    private var delegate: TaskDelegate? = null
    private var error: Exception? = null

    public interface TaskDelegate {
        fun onAccountReceived(account: FullAccount);
        fun onError(error: Exception);
    }

    fun DropboxAccountTask(dbxClient: DbxClientV2, delegate: TaskDelegate) {
        this.dbxClient = dbxClient;
        this.delegate = delegate;
    }


    override fun doInBackground(vararg params: Void?): FullAccount? {
        error = try {
            //get the users FullAccount
            return dbxClient!!.users().currentAccount
        } catch (e: DbxException) {
            e.printStackTrace()
            e
        }
        return null
    }

    override fun onPostExecute(account: FullAccount?) {
        super.onPostExecute(account)
        if (account != null && error == null) {
            //User Account received successfully
            delegate!!.onAccountReceived(account)
        } else {
            // Something went wrong
            delegate!!.onError(error!!)
        }
    }


}

object DropboxClient {

    fun getClient(ACCESS_TOKEN: String?): DbxClientV2 {
        // Create Dropbox client
        val config = DbxRequestConfig("dropbox/tickt-app", "en_US")
        return DbxClientV2(config, ACCESS_TOKEN)
    }
}
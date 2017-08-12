package android.example.com.squawker.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by pmvb on 17-08-12.
 */
class SquawkFirebaseInstanceService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.v("SquawkFirebaseInstanceService", token)

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package jp.loiterjoven.nslj360

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = PusherOptions()
        options.setCluster(Constant.PUSHER_APP_CLUSTER);

        val pusher = Pusher(Constant.PUSHER_APP_KEY, options)


        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i("Pusher", "State changed from ${change.previousState} to ${change.currentState}")
            }

            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
                Log.i("Pusher", "There was a problem connecting! code ($code), message ($message), exception($e)")
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("nslj360-channel")
        channel.bind("test-event") { event ->
            Log.i("Pusher","Received event with data: $event")
            //  {event=my-event, data={"message":"hello world"}, channel=my-channel}
            runOnUiThread {
                Toast.makeText(applicationContext, event.data.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
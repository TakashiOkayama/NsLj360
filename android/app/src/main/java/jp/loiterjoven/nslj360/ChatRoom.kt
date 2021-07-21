package jp.loiterjoven.nslj360

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.activity_chat_room.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRoom : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_COUNT = "numb"
    }

    private lateinit var contactName: String
    private lateinit var contactId: String
    private var contactNumb: Int = -1
    lateinit var nameOfChannel: String
    val mAdapter = ChatRoomAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        fetchExtras()
        setupRecyclerView()
        subscribeToChannel()
        setupClickListener()
    }

    private fun fetchExtras() {
        contactName = intent.extras?.getString(ChatRoom.EXTRA_NAME)!!
        contactId = intent.extras?.getString(ChatRoom.EXTRA_ID)!!
        contactNumb = intent.extras?.getInt(ChatRoom.EXTRA_COUNT)!!
    }

    private fun setupRecyclerView() {
        with(recyclerViewChat) {
            layoutManager = LinearLayoutManager(this@ChatRoom)
            adapter = mAdapter
        }
    }

    private fun subscribeToChannel() {
        val authorizer = HttpAuthorizer("${Constant.BE_SERVER_BASE_URL}pusher/auth/private")
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster(Constant.PUSHER_APP_CLUSTER)

        val pusher = Pusher(Constant.PUSHER_APP_KEY, options)
        pusher.connect()

        nameOfChannel = if (Singleton.getInstance().currentUser.count > contactNumb) {
            "private-nslj360-" + Singleton.getInstance().currentUser.id + "-" + contactId
        } else {
            "private-nslj360-" + contactId + "-" + Singleton.getInstance().currentUser.id
        }

        Log.i("ChatRoom", nameOfChannel)

        pusher.subscribePrivate(nameOfChannel, object : PrivateChannelEventListener {
//            override fun onEvent(channelName: String?, eventName: String?, data: String?) {
//                val obj = JSONObject(data)
//                val messageModel = MessageModel(obj.getString("message"), obj.getString("sender_id"))
//
//                runOnUiThread {
//                    mAdapter.add(messageModel)
//                }
//            }

            override fun onEvent(event: PusherEvent?) {
                val obj = JSONObject(event?.data!!)
                val messageModel = MessageModel(obj.getString("message"), obj.getString("sender_id"))

                runOnUiThread {
                    mAdapter.add(messageModel)
                }
            }

            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {}
            override fun onSubscriptionSucceeded(p0: String?) {}
        }, "new-message")
    }

    private fun setupClickListener() {
        sendButton.setOnClickListener{
            if (editText.text.isNotEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("message", editText.text.toString())
                jsonObject.put("channel_name", nameOfChannel)
                jsonObject.put("sender_id", Singleton.getInstance().currentUser.id)

                val jsonBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonObject.toString()
                )

                RetrofitInstance.retrofit.sendMessage(jsonBody).enqueue(object: Callback<String> {
                    override fun onFailure(call: Call<String>?, t: Throwable?) {}
                    override fun onResponse(call: Call<String>?, response: Response<String>?) {}
                })

                editText.text.clear()
                hideKeyBoard()
            }

        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus

        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
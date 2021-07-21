package jp.loiterjoven.nslj360

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.User
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.activity_contact_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity: AppCompatActivity(),
    ContactRecyclerAdapter.UserClickListener {

    private val mAdapter = ContactRecyclerAdapter(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        setupRecyclerView()
        fetchUsers()
        subscribeToChannel()
    }

    private fun setupRecyclerView() {
        with(recyclerViewUserList) {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
            adapter = mAdapter
        }
    }

    private fun fetchUsers() {
        RetrofitInstance.retrofit.getUsers().enqueue(object : Callback<List<UserModel>> {
            override fun onFailure(call: Call<List<UserModel>>?, t: Throwable?) {}
            override fun onResponse(call: Call<List<UserModel>>?, response: Response<List<UserModel>>?) {
                for (user in response!!.body()!!) {
                    if (user.id != Singleton.getInstance().currentUser.id) {
                        mAdapter.add(user)
                    }
                }
            }
        })
    }

    private fun subscribeToChannel() {
        Log.d("ContactListActivity", "subscribeToChannel")

        val authorizer = HttpAuthorizer("${Constant.BE_SERVER_BASE_URL}pusher/auth/presence")
        val options = PusherOptions().setAuthorizer(authorizer)
        options.setCluster(Constant.PUSHER_APP_CLUSTER)

        val pusher = Pusher(Constant.PUSHER_APP_KEY, options)
        pusher.connect()

        pusher.subscribePresence("presence-nslj360-channel", object : PresenceChannelEventListener {
            override fun onUsersInformationReceived(p0: String?, users: MutableSet<User>?) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::onUsersInformationReceived")
                for (user in users!!) {
                    if (user.id!=Singleton.getInstance().currentUser.id){
                        runOnUiThread {
                            mAdapter.showUserOnline(user.toUserModel())
                        }
                    }
                }
            }

            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::onAuthenticationFailure")
                if (p0 != null) {
                    Log.e("ContactListActivity", p0)
                }
                if (p1 != null) {
                    Log.e("ContactListActivity", p1.toString())
                }
            }
            override fun onEvent(event: PusherEvent?) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::onEvent")
            }

            override fun onSubscriptionSucceeded(p0: String?) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::onSubscriptionSucceeded")
            }

            override fun userSubscribed(channelName: String, user: User) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::userSubscribed")
                Log.d("ContactListActivity", user.toString())
                runOnUiThread {
                    mAdapter.showUserOnline(user.toUserModel())
                }
            }

            override fun userUnsubscribed(channelName: String, user: User) {
                Log.d("ContactListActivity", "PresenceChannelEventListener::userUnsubscribed")
                runOnUiThread {
                    mAdapter.showUserOffline(user.toUserModel())
                }
            }
        })
    }

    override fun onUserClicked(user: UserModel) {
        val intent = Intent(this, ChatRoom::class.java)
        intent.putExtra(ChatRoom.EXTRA_ID,user.id)
        intent.putExtra(ChatRoom.EXTRA_NAME,user.name)
        intent.putExtra(ChatRoom.EXTRA_COUNT,user.count)
        startActivity(intent)
    }
}
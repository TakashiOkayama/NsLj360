package jp.loiterjoven.nslj360

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(applicationContext, "Start Application", Toast.LENGTH_SHORT).show()

        val action = intent.action
        if (Intent.ACTION_VIEW == action) {
            val uri = intent.getData()
            uri?.let { it
                Log.d("StarterActivity", it.toString())
                when (it.toString()) {
                    "jp.loiterjoven.nslj360://test/start" -> {
                        startActivity(Intent(this@StarterActivity, LoginActivity::class.java))
                    }
                    else -> {
                        Toast.makeText(applicationContext, "Invalid URL!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
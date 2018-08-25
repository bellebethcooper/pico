package co.hellocode.micro

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_timeline.*
import org.json.JSONObject

class TimelineActivity : AppCompatActivity() {

    var progress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        this.progress = spinner("Loading...")
        this.progress?.show()
        getTimeline()
    }

    fun spinner(message: String): ProgressDialog {
        val spinner = ProgressDialog(this)
        spinner.setMessage(message)
        spinner.isIndeterminate = true
        return spinner
    }

    private fun getTimeline() {
        val url = "https://micro.blog/posts/all"
        val rq = object : StringRequest(
                Request.Method.GET,
                url,
                Response.Listener<String> { response ->
                    Log.i("MainActivity", "resp: $response")
                    this.progress?.hide()
                    val json = JSONObject(response)
                    Log.i("MainActivity", "json: $json")
                },
                Response.ErrorListener { error ->
                    Log.i("MainActivity", "err: $error msg: ${error.message}")
                    this.progress?.hide()
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = this@TimelineActivity.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(rq)
    }
}

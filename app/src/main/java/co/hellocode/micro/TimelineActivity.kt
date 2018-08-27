package co.hellocode.micro

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.Menu
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_timeline.*
import org.json.JSONArray
import org.json.JSONObject
import android.text.style.ImageSpan
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class TimelineActivity : AppCompatActivity() {

    var progress: ProgressDialog? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: TimelineRecyclerAdapter
    private var posts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        setSupportActionBar(toolbar)
        this.linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = this.linearLayoutManager
        this.adapter = TimelineRecyclerAdapter(this.posts)
        recyclerView.adapter = this.adapter

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
        val rq = object : JsonObjectRequest(
                url,
                null,
                Response.Listener<JSONObject> { response ->
                    Log.i("MainActivity", "resp: $response")
                    val items = response["items"] as JSONArray
                    for (i in 0 until items.length()) {
                        val item = items[i] as JSONObject
                        val id = (item["id"] as String).toInt()
                        val text = item["content_html"] as String
                        var mentions: ArrayList<String> = arrayListOf()
                        val regex = Regex("[@]\\w+")
                        val all = regex.findAll(text)
                        for (match in all) {
                            mentions.add(match.value)
                        }
                        val datePublished = item["date_published"] as String
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+'ss':'ss")
                        val date = dateFormat.parse(datePublished)
                        val author = (item["author"] as JSONObject)
                        val authorName : String = author.getString("name")
                        val username = (author["_microblog"] as JSONObject).getString("username")
                        val microblogData = (item["_microblog"] as JSONObject)
//                        Log.i("MainActivity", microblogData.toString())
                        val isConversation: Boolean = microblogData.getBoolean("is_conversation")
//                        Log.i("MainActivity", "item: $text")
                        this.posts.add(Post(id, text, authorName, username, isConversation, date, mentions))
                    }
                    this.adapter.notifyDataSetChanged()
                    this.progress?.hide()
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

package co.hellocode.micro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import co.hellocode.micro.R.id.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timeline.*
import kotlinx.android.synthetic.main.baselayout_timeline.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


open class BaseTimelineActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: TimelineRecyclerAdapter
    private var posts = ArrayList<Post>()
    private lateinit var refresh: SwipeRefreshLayout
    open var url = "https://micro.blog/posts/all"
    open var title = "Timeline"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        setSupportActionBar(toolbar)
        this.linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = this.linearLayoutManager
        this.adapter = TimelineRecyclerAdapter(this.posts)
        recyclerView.adapter = this.adapter
        toolbar.title = title

        fab.setOnClickListener { view ->
            val intent = Intent(this, NewPostActivity::class.java)
            startActivityForResult(intent, NEW_POST_REQUEST_CODE)
        }

        this.refresh = refresher
        this.refresh.setOnRefreshListener { refresh() }
        initialLoad()
    }

    open fun initialLoad() {
        Log.i("BaseTimeline", "initialLoad")
        this.refresh.isRefreshing = true
        refresh()
    }

    private fun refresh() {
        Log.i("BaseTimeline", "refresh")
        getTimeline()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            refresh()
        }
    }

    fun prefs() : SharedPreferences {
        return getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    private fun getTimeline() {
        Log.i("BaseTimeline", "getTimeline")
        val rq = object : JsonObjectRequest(
                this.url,
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
                    this.refresh.isRefreshing = false
                },
                Response.ErrorListener { error ->
                    Log.i("MainActivity", "err: $error msg: ${error.message}")
                    this.refresh.isRefreshing = false
                    // TODO: Handle error
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = prefs()
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(rq)
    }
}

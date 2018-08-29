package co.hellocode.micro.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.hellocode.micro.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.baselayout_timeline.*
import kotlinx.android.synthetic.main.baselayout_timeline.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap

open class BaseTimelineFragment: Fragment() {
    open var url = "https://micro.blog/posts/all"
    open var title = "Timeline"
    open lateinit var linearLayoutManager: LinearLayoutManager
    open lateinit var adapter: TimelineRecyclerAdapter
    open var posts = ArrayList<Post>()
    open lateinit var refresh: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.baselayout_timeline, container, false)
        this.linearLayoutManager = LinearLayoutManager(context)
        view.recyclerView.layoutManager = this.linearLayoutManager
        this.adapter = TimelineRecyclerAdapter(this.posts)
        view.recyclerView.adapter = this.adapter
        this.refresh = view.refresher
        this.refresh.setOnRefreshListener { refresh() }
        return view
    }

    override fun onStart() {
        super.onStart()
        Log.i("TimelineFragment", "onStart")
        initialLoad()
    }

    open fun initialLoad() {
        Log.i("BaseTimeline", "initialLoad")
        this.refresh.isRefreshing = true
        refresh()
    }

    open fun refresh() {
        Log.i("BaseTimeline", "refresh")
        getTimeline()
    }

    fun prefs() : SharedPreferences? {
        return this.activity?.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    open fun getTimeline(url: String = this.url) {
        Log.i("BaseTimeline", "getTimeline url: $url")
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
        val queue = Volley.newRequestQueue(this.activity)
        queue.add(rq)
    }
}
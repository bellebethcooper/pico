package co.hellocode.micro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import co.hellocode.micro.Utils.PREFS_FILENAME
import co.hellocode.micro.Utils.TOKEN
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.baselayout_timeline.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class ConversationActivity() : BaseTimelineActivity() {
    override var url = "https://micro.blog/posts/conversation?id="
    override var title = "Conversation"
    var startText = ""
    private var postID: Int = 0

    override fun initialLoad() {
        this.postID = intent.getIntExtra("@string/reply_intent_extra_postID", 0)
        this.url = this.url + postID.toString()
        super.initialLoad()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.adapter = TimelineRecyclerAdapter(this.posts, canShowConversations = false)
        conversation_recyclerView.adapter = this.adapter

        reply_button.setOnClickListener {
            submitPost(reply_button)
        }

        // set up start text for reply box
        val author = intent.getStringExtra("@string/reply_intent_extra_author")
        this.startText += "@$author "
        val mentions = intent.getStringArrayListExtra("@string/reply_intent_extra_mentions")
        if (mentions != null) {
            for (mention in mentions) {
                this.startText += "$mention "
            }
        }
        reply_view.setOnFocusChangeListener { v, hasFocus ->
            if (v === reply_view && hasFocus && (v.text == null || v.text.toString() == "")) {
                v.setText(this.startText)
            }
        }
    }

    override fun createPosts(items: JSONArray) {
        this.posts.clear()
        for (i in 0 until items.length()) {
            val item = items[i] as JSONObject
            this.posts.add(Post(item))
        }
        this.posts.reverse()
    }

    override fun contentView(): Int {
        return R.layout.activity_conversation
    }

    override fun recycler() : RecyclerView {
        return conversation_recyclerView
    }

    private fun submitPost(view: View) {
        spinner.visibility = View.VISIBLE
        reply_button.visibility = View.GONE

        val text = reply_view.text.toString()
        val queue = Volley.newRequestQueue(this)
        val postUrl = "https://micro.blog/micropub"
        val replyUrl = "https://micro.blog/posts/reply"
        var url = postUrl

        // Use the reply URL with post ID appended if this post is a reply
        if (this.postID != 0) {
            url = replyUrl + "?id=${this.postID}"
        }

        val rq = object : StringRequest(
                Request.Method.POST,
                url,
                Response.Listener<String> { response ->
                    Log.i("MainActivity", "resp: $response")
                    Snackbar.make(view, "Success!", Snackbar.LENGTH_LONG).show()
                    reply_view.setText("")
                    spinner.visibility = View.GONE
                    reply_button.visibility = View.VISIBLE
                },
                Response.ErrorListener { error ->
                    Log.i("MainActivity", "err: $error msg: ${error.message}")
                    Snackbar.make(view, "Error: $error", Snackbar.LENGTH_LONG).show()
                    spinner.visibility = View.GONE
                    reply_button.visibility = View.VISIBLE
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = this@ConversationActivity.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                Log.i("MainActivity", "getParams")
                val params = HashMap<String, String>()
                params["h"] = "entry"
                params["text"] = text
                return params
            }
        }
        // set timeout to zero so Volley won't send multiple of the same request
        // seems like a Volley bug: https://groups.google.com/forum/#!topic/volley-users/8PE9dBbD6iA
        rq.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(rq)
    }
}

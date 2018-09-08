package co.hellocode.micro.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import co.hellocode.micro.Post
import co.hellocode.micro.utils.PREFS_FILENAME
import co.hellocode.micro.utils.TOKEN
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_new_post.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

const val TIMELINE_URL = ""
const val MENTIONS_URL = ""
const val DISCOVER_URL = ""
const val PHOTOS_URL = ""
const val CONVERSATION_URL = ""
const val PROFILE_URL = ""
const val FOLLOW_URL = ""
const val UNFOLLOW_URL = ""
const val NEW_POST_URL = ""
const val REPLY_URL = ""

sealed class GetEndpoint {
    class Timeline : GetEndpoint()
    class Mentions : GetEndpoint()
    class Discover : GetEndpoint()
    class Photos : GetEndpoint()
    class Profile(val username: String) : GetEndpoint()
    class Conversation(val postID: Int) : GetEndpoint()
}

sealed class PostEndpoint {
    class NewPost(val content: String) : PostEndpoint()
    class Follow(val username: String) : PostEndpoint()
    class Unfollow(val username: String) : PostEndpoint()
    class Reply(val postID: Int, val content: String) : PostEndpoint()
}

class APIService {

    public fun get(endpoint: GetEndpoint, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener, context: Context) {
        var url = "https://micro.blog/posts/all"
        when (endpoint) {
            is GetEndpoint.Timeline -> {
                url = "https://micro.blog/posts/all"
            }
            is GetEndpoint.Mentions -> {
                url = "https://micro.blog/posts/mentions"
            }
            is GetEndpoint.Discover -> {
                url = "https://micro.blog/posts/discover"
            }
            is GetEndpoint.Photos -> {
                url = "https://micro.blog/posts/photos"
            }
            is GetEndpoint.Profile -> {
                url = "https://micro.blog/posts/" + endpoint.username
            }
            is GetEndpoint.Conversation -> {
                url = "https://micro.blog/posts/conversation?id=" + endpoint.postID
            }
        }
        get(url, listener, errorListener, context)
    }

    public fun postTo(endpoint: PostEndpoint, listener: Response.Listener<String>, errorListener: Response.ErrorListener, context: Context) {
        var url = "https://micro.blog/micropub"
        val params = HashMap<String, String>()
        when (endpoint) {
            is PostEndpoint.NewPost -> {
                url = "https://micro.blog/micropub"
                params["h"] = "entry"
                params["content"] = endpoint.content
            }
            is PostEndpoint.Follow -> {
                url = "https://micro.blog/users/follow"
                params["username"] = endpoint.username
            }
            is PostEndpoint.Unfollow -> {
                url = "https://micro.blog/users/unfollow"
                params["username"] = endpoint.username
            }
            is PostEndpoint.Reply -> {
                url = "https://micro.blog/posts/reply?id=" + endpoint.postID
                params["h"] = "entry"
                params["text"] = endpoint.content
            }
        }
        post(url, params, listener, errorListener, context)
    }

    private fun get(url: String, listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener, context: Context) {
        val rq = object : JsonObjectRequest(
                url,
                null,
                listener,
                errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(rq)
    }

    private fun post(toUrl: String, params: HashMap<String, String>, listener: Response.Listener<String>, errorListener: Response.ErrorListener, context: Context) {
        val rq = object : StringRequest(
                Request.Method.POST,
                toUrl,
                listener,
                errorListener
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return params
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(rq)
    }
}
package co.hellocode.micro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import co.hellocode.micro.Utils.TOKEN
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.baselayout_timeline.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class ProfileActivity : BaseTimelineActivity() {

    override var url = "https://micro.blog/posts/"
    override var title = ""
    var following = false
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab.visibility = View.GONE
    }

    override fun initialLoad() {
        this.username = intent.getStringExtra("username")
        this.url = this.url + this.username
        super.initialLoad()
    }

    override fun contentView(): Int {
        return R.layout.activity_profile
    }

    override fun getRequestComplete(response: JSONObject) {
        super.getRequestComplete(response)
        val author = response.getJSONObject("author")
        name.text = author.getString("name")
        website.text = author.getString("url")
        val avatarURL = author.getString("avatar")
        Picasso.get().load(avatarURL).transform(CropCircleTransformation()).into(profile_avatar)
        val microBlog = response.getJSONObject("_microblog")
        this.usernameView.text = microBlog.getString("username")
        bio.text = microBlog.getString("bio")
        val isYou = microBlog.getBoolean("is_you")
        if (isYou) {
            follow_button.visibility = View.GONE
        } else {
            this.following = microBlog.getBoolean("is_following")
            follow_button.text = if (this.following == true) "Unfollow" else "Follow"
        }
        this.follow_button.setOnClickListener { followButtonTapped(this.username) }
    }

    fun followButtonTapped(username: String) {
        val followURL = if (this.following) {
            "https://micro.blog/users/unfollow"
        } else {
            "https://micro.blog/users/follow"
        }
        val rq = object : StringRequest(
                Request.Method.POST,
                followURL,
                Response.Listener<String> { response ->
                    if (this.following) {
                        // this.following is already true, so we just unfollowed
                        this.follow_button.text = "Follow"
                        this.following = false
                    } else {
                        // this.following is false, so we just followed
                        this.follow_button.text = "Unfollow"
                        this.following = true
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("ProfileAct", "err following or unfollowing: $error msg: ${error.message}")
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = prefs()
                val token: String? = prefs.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                Log.i("MainActivity", "getParams")
                val params = HashMap<String, String>()
                params["username"] = username
                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(rq)
    }
}

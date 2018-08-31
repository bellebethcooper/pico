package co.hellocode.micro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject

class Profile : BaseTimelineActivity() {

    override var url = "https://micro.blog/posts/"
    override var title = ""

    override fun initialLoad() {
        val username = intent.getStringExtra("username")
        Log.i("Profile", "username: $username")
        this.url = this.url + username
        super.initialLoad()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    override fun getRequestComplete(response: JSONObject) {
        super.getRequestComplete(response)
        val author = response.getJSONObject("author")
        name.text = author.getString("name")
        website.text = author.getString("url")
        val avatarURL = author.getString("avatar")
        Picasso.get().load(avatarURL).into(profile_avatar)
        val microBlog = response.getJSONObject("_microblog")
        username.text = microBlog.getString("username")
        bio.text = microBlog.getString("bio")
        follow_button.text = if (microBlog.getBoolean("is_following") == true) "Unfollow" else "Follow"
    }
}

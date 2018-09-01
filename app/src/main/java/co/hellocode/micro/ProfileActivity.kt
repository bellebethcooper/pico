package co.hellocode.micro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.baselayout_timeline.*
import org.json.JSONObject

class ProfileActivity : BaseTimelineActivity() {

    override var url = "https://micro.blog/posts/"
    override var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab.visibility = View.GONE
    }

    override fun initialLoad() {
        val username = intent.getStringExtra("username")
        this.url = this.url + username
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
        username.text = microBlog.getString("username")
        bio.text = microBlog.getString("bio")
        val isYou = microBlog.getBoolean("is_you")
        if (isYou) {
            follow_button.visibility = View.GONE
        } else {
            follow_button.text = if (microBlog.getBoolean("is_following") == true) "Unfollow" else "Follow"
        }
    }
}

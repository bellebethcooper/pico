package co.hellocode.micro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.baselayout_timeline.*
import kotlin.math.log

class ConversationActivity() : BaseTimelineActivity() {
    override var url = "https://micro.blog/posts/conversation?id="
    override open var title = ""

    override fun initialLoad() {
        val id = intent.getIntExtra("postID", 0)
        Log.i("Conversation", "id: $id")
        this.url = this.url + id.toString()
        super.initialLoad()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.adapter = ConversationRecyclerAdapter(this.posts)
        recyclerView.adapter = this.adapter
    }
}

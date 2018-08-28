package co.hellocode.micro
import android.content.Intent
import android.support.constraint.R.id.gone
import android.support.constraint.R.id.parent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_timeline.*
import kotlinx.android.synthetic.main.timeline_item.view.*

class TimelineRecyclerAdapter(private val posts: ArrayList<Post>) : RecyclerView.Adapter<TimelineRecyclerAdapter.PostHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimelineRecyclerAdapter.PostHolder {
        val inflatedView = p0.inflate(R.layout.timeline_item, false)
        return PostHolder(inflatedView)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(p0: TimelineRecyclerAdapter.PostHolder, p1: Int) {
        val itemPost = posts[p1]
        p0.bindPost(itemPost)
    }

    class PostHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var post: Post? = null

        init {
            v.setOnClickListener(this)
            v.setOnLongClickListener {
                Log.i("TimelineRecycler", "long press!")
                if (post == null) { return@setOnLongClickListener false }

                val intent = Intent(it.context, MainActivity::class.java)
                var id = post?.ID
                Log.i("Recycler", "id: $id")
                intent.putExtra("postID", post?.ID)
                intent.putExtra("author", post?.username)
                if (post?.mentions != null) {
                    intent.putStringArrayListExtra("mentions", post?.mentions)
                }
                it.context.startActivity(intent)
                true
            }
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        fun bindPost(post: Post) {
            this.post = post
            view.itemText.text = post.html
            view.author.text = post.author
            if (!post.isConversation) {
                view.conversationButton.visibility = View.GONE
            }
        }

        companion object {
            private val POST_KEY = "POST"
        }
    }
}
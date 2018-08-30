package co.hellocode.micro
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import co.hellocode.micro.Utils.inflate
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
                if (post == null) { return@setOnLongClickListener false }
                newPostIntent(it)
                true
            }
        }

        override fun onClick(v: View) {
            postDetailIntent(v)
        }

        private fun newPostIntent(view: View) {
            val intent = Intent(view.context, NewPostActivity::class.java)
            post = post
            var id = post?.ID
            Log.i("Recycler", "id: $id")
            intent.putExtra("postID", post?.ID)
            intent.putExtra("author", post?.username)
            if (post?.mentions != null) {
                intent.putStringArrayListExtra("mentions", post?.mentions)
            }
            view.context.startActivity(intent)
        }

        private fun postDetailIntent(view: View) {
            Log.i("Recycler", "postDetailIntent")
            val intent = Intent(view.context, ConversationActivity::class.java)
            this.post = post
            var id = post?.ID
            Log.i("Recycler postDetailIntent", "id: $id")
            intent.putExtra("postID", post?.ID)
            view.context.startActivity(intent)
        }

        fun bindPost(post: Post) {
            this.post = post
            view.itemText.text = post.getParsedContent(view.context)
            view.author.text = post.authorName
            view.username.text = "@${post.username}"
            if (!post.isConversation) {
                view.conversationButton.visibility = View.GONE
            } else{
                view.conversationButton.visibility = View.VISIBLE
            }

            view.timestamp.text = DateUtils.getRelativeTimeSpanString(view.context, post.date.time)
        }

        companion object {
            private val POST_KEY = "POST"
        }
    }
}
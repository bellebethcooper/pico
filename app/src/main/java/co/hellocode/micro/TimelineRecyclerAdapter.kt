package co.hellocode.micro

import android.content.Intent
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import co.hellocode.micro.Utils.inflate
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.layout_post_image.view.*
import kotlinx.android.synthetic.main.timeline_item.view.*

open class TimelineRecyclerAdapter(private val posts: ArrayList<Post>, private val canShowConversations: Boolean = true) : RecyclerView.Adapter<TimelineRecyclerAdapter.PostHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimelineRecyclerAdapter.PostHolder {
        val inflatedView = p0.inflate(R.layout.timeline_item, false)
        return PostHolder(inflatedView, canShowConversations)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(p0: TimelineRecyclerAdapter.PostHolder, p1: Int) {
        val itemPost = posts[p1]
        p0.bindPost(itemPost)
    }

    class PostHolder(v: View, private var canShowConversations: Boolean) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var post: Post? = null

        init {
            if (this.canShowConversations) {
                v.setOnClickListener(this)
            }
            v.setOnLongClickListener {
                if (post == null) {
                    return@setOnLongClickListener false
                }
                newPostIntent(it)
                true
            }
        }

        override fun onClick(v: View) {
            if (this.canShowConversations) {
                postDetailIntent(v)
            }
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
            // remove any image views leftover from reusing views
            for (i in 0 until view.post_layout.childCount) {
                val v = view.post_layout.getChildAt(i)
                if (v is ImageView) {
                    view.post_layout.removeViewAt(i)
                }
            }
            // and remove user avatar image
            view.avatar.setImageDrawable(null)

            this.post = post
            view.itemText.text = post.getParsedContent(view.context)
            view.author.text = post.authorName
            view.username.text = "@${post.username}"
            if (!post.isConversation) {
                view.conversationButton.visibility = View.GONE
            } else {
                view.conversationButton.visibility = View.VISIBLE
            }

            view.timestamp.text = DateUtils.getRelativeTimeSpanString(view.context, post.date.time)

            Picasso.get().load(post.authorAvatarURL).transform(CropCircleTransformation()).into(view.avatar)

            for (i in post.imageSources) {
                val imageView = LayoutInflater.from(view.context).inflate(
                        R.layout.layout_post_image,
                        null,
                        false
                )
                view.post_layout.addView(imageView)
                Picasso.get().load(i).into(imageView.post_image)
            }
        }

        companion object {
            private val POST_KEY = "POST"
        }
    }
}
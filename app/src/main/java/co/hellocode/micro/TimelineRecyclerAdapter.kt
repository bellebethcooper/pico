package co.hellocode.micro
import android.support.constraint.R.id.gone
import android.support.constraint.R.id.parent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.timeline_item.view.*

class TimelineRecyclerAdapter(private val posts: ArrayList<Post>) : RecyclerView.Adapter<TimelineRecyclerAdapter.PostHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimelineRecyclerAdapter.PostHolder {
        val inflatedView = p0.inflate(R.layout.timeline_item, false)
        return PostHolder(inflatedView)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(p0: TimelineRecyclerAdapter.PostHolder, p1: Int) {
        val itemPhoto = posts[p1]
        p0.bindPost(itemPhoto)
    }

    class PostHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var post: Post? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        fun bindPost(post: Post) {
            this.post = post

//            Log.i("TimelineRecyclerAdapter", "html: $html")
            view.itemText.text = post.html
            view.author.text = post.author
            if (!post.isConversation) {
                view.conversationButton.visibility = View.GONE
            }
//            for (img in html.getSpans(0,
//                    html.length(), ImageSpan::class.java)) {
//                if (!getImageFile(img).isFile()) {
//
//                    // here you have to download the file
//                }
//
//                fun getImageFile(img: ImageSpan) {}
//                Picasso.get().load(img.source).into()
//            }
//
//        }
        }

        companion object {
            private val POST_KEY = "POST"
        }
    }
}
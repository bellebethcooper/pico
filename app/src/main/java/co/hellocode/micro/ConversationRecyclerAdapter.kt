package co.hellocode.micro

class ConversationRecyclerAdapter(private val posts: ArrayList<Post>) : TimelineRecyclerAdapter(posts) {

    init {
        // Make convo read top to bottom
        this.posts.reverse()
    }

}
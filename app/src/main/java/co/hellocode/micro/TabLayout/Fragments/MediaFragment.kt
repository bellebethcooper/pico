package co.hellocode.micro.TabLayout.Fragments

class MediaFragment : BaseTimelineFragment() {

    override var url = "https://micro.blog/posts/photos"
    override open var title = "Photos"

    companion object {
        fun newInstance(): MediaFragment = MediaFragment()
    }

    override fun refresh() {
        getTimeline(this.url)
    }
}
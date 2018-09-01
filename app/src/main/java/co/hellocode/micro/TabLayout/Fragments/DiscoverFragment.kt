package co.hellocode.micro.TabLayout.Fragments

class DiscoverFragment : BaseTimelineFragment() {

    override var url = "https://micro.blog/posts/discover"
    override open var title = "Discover"

    companion object {
        fun newInstance(): DiscoverFragment = DiscoverFragment()
    }

    override fun refresh() {
        getTimeline(this.url)
    }
}
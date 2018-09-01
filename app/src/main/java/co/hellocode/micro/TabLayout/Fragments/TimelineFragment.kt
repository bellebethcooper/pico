package co.hellocode.micro.TabLayout.Fragments

class TimelineFragment : BaseTimelineFragment() {

    override var url = "https://micro.blog/posts/all"
    override open var title = "Timeline"

    companion object {
        fun newInstance(): TimelineFragment = TimelineFragment()
    }

    override fun refresh() {
        getTimeline(this.url)
    }
}
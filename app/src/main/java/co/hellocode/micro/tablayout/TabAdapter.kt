package co.hellocode.micro.tablayout

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import co.hellocode.micro.tablayout.fragments.DiscoverFragment
import co.hellocode.micro.tablayout.fragments.MediaFragment
import co.hellocode.micro.tablayout.fragments.MentionsFragment
import co.hellocode.micro.tablayout.fragments.TimelineFragment

class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> TimelineFragment.newInstance()
        1 -> MentionsFragment.newInstance()
        2 -> DiscoverFragment.newInstance()
        3 -> MediaFragment.newInstance()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Timeline"
        1 -> "Mentions"
        2 -> "Discover"
        3 -> "Photos"
        else -> ""
    }

    override fun getCount(): Int = 4
}
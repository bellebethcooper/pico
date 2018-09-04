package co.hellocode.micro.Utils

import co.hellocode.micro.R.id.fab
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener
import android.support.v4.view.ViewCompat.getTranslationY
import android.support.v4.view.ViewCompat.setTranslationY
import android.opengl.ETC1.getHeight
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.annotation.NonNull
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import co.hellocode.micro.R


class FabOffsetter(private val parent: CoordinatorLayout, private val fab: FloatingActionButton) : AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // let's see how far along the way the appBarLayout is
        // (if displacementFraction == 0.0f then no displacement, appBar is fully expanded;
        //  if displacementFraction == 1.0f then full displacement, appBar is totally collapsed)
        val toolbarHeight = appBarLayout.findViewById<Toolbar>(R.id.toolbar).height
        val displacementFraction = -verticalOffset / toolbarHeight.toFloat()

        val diff = parent.height - fab.top
        fab.translationY = diff * displacementFraction

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as FabOffsetter?
        return parent == that!!.parent && fab == that.fab

    }

    override fun hashCode(): Int {
        var result = parent.hashCode()
        result = 31 * result + fab.hashCode()
        return result
    }
}
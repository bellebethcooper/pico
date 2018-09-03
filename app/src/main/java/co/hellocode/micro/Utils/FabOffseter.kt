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
import co.hellocode.micro.R


class FabOffsetter(private val parent: CoordinatorLayout, private val fab: FloatingActionButton) : AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        // fab should scroll out down in sync with the appBarLayout scrolling out up.
        // let's see how far along the way the appBarLayout is
        // (if displacementFraction == 0.0f then no displacement, appBar is fully expanded;
        //  if displacementFraction == 1.0f then full displacement, appBar is totally collapsed)
        val displacementFraction = -verticalOffset / appBarLayout.height.toFloat()

        // need to separate translationY on the fab that comes from this behavior
        // and one that comes from other sources
        // translationY from this behavior is stored in a tag on the fab
        val translationYFromThis: Float = fab.getTag(R.id.fab_translationY_from_AppBarBoundFabBehavior) as Float? ?: 0.0f

        // top position, accounting for translation not coming from this behavior
        val topUntranslatedFromThis = fab.top + fab.translationY - translationYFromThis

        // total length to displace by (from position uninfluenced by this behavior) for a full appBar collapse
        val fullDisplacement = parent.bottom - topUntranslatedFromThis

        // calculate and store new value for displacement coming from this behavior
        val newTranslationYFromThis = fullDisplacement * displacementFraction
        fab.setTag(R.id.fab_translationY_from_AppBarBoundFabBehavior, newTranslationYFromThis)

        // update translation value by difference found in this step
        fab.translationY = newTranslationYFromThis - translationYFromThis + fab.translationY
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
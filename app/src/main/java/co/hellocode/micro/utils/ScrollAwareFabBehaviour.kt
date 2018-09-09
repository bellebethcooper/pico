package co.hellocode.micro.utils

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import co.hellocode.micro.utils.FabOffsetter



class ScrollAwareFabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun layoutDependsOn(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
//        return dependency is AppBarLayout
        if (dependency is AppBarLayout) {
            dependency.addOnOffsetChangedListener(FabOffsetter(parent, fab))
        }
        return dependency is AppBarLayout || super.layoutDependsOn(parent, fab, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
//        if (dependency is AppBarLayout) {
//            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
//            Log.i("ScrollAwareFab", "layout: ${lp.bottomMargin} fab height: ${fab.height} ${fab.width}")
//            val fabBottomMargin = lp.bottomMargin
//            val distanceToScroll = fab.height + fabBottomMargin
//            val ratio = dependency.getY() / toolbarHeight.toFloat()
//            fab.translationY = -distanceToScroll * ratio
//        }
//        return true
        if (dependency is AppBarLayout) {
            // if the dependency is an AppBarLayout, do not allow super to react on that
            // we don't want that behavior
            return true;
        }
        return super.onDependentViewChanged(parent, fab, dependency);
    }


}
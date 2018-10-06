package me.venko.presencetracker.utils

import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import me.venko.presencetracker.R

/**
 * @author Victor Kosenko
 *
 */

private fun FragmentManager.performTransaction(fragment: Fragment,
                                               @IdRes containerViewId: Int,
                                               tag: String = fragment.javaClass.name,
                                               allowStateLoss: Boolean = false,
                                               skipBackStack: Boolean = false,
                                               @AnimRes enterAnimation: Int = 0,
                                               @AnimRes exitAnimation: Int = 0,
                                               @AnimRes popEnterAnimation: Int = 0,
                                               @AnimRes popExitAnimation: Int = 0) {
    val ft = beginTransaction()
            .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
            .replace(containerViewId, fragment, tag)
    if (!skipBackStack) {
        ft.addToBackStack(tag)
    }
    if (!isStateSaved) {
        ft.commit()
    } else if (allowStateLoss) {
        ft.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment,
                                      @IdRes containerViewId: Int,
                                      tag: String = fragment.javaClass.name,
                                      allowStateLoss: Boolean = false,
                                      skipBackStack: Boolean = false,
                                      @AnimRes enterAnimation: Int = 0,
                                      @AnimRes exitAnimation: Int = 0,
                                      @AnimRes popEnterAnimation: Int = 0,
                                      @AnimRes popExitAnimation: Int = 0) {
    supportFragmentManager.performTransaction(
            fragment,
            containerViewId,
            tag,
            allowStateLoss,
            skipBackStack,
            enterAnimation,
            exitAnimation,
            popEnterAnimation,
            popExitAnimation
    )
}

fun Fragment.replaceFragment(fragment: Fragment,
                             @IdRes containerViewId: Int,
                             tag: String = fragment.javaClass.name,
                             allowStateLoss: Boolean = false,
                             skipBackStack: Boolean = false,
                             @AnimRes enterAnimation: Int = 0,
                             @AnimRes exitAnimation: Int = 0,
                             @AnimRes popEnterAnimation: Int = 0,
                             @AnimRes popExitAnimation: Int = 0) {
    fragmentManager?.performTransaction(
            fragment,
            containerViewId,
            tag,
            allowStateLoss,
            skipBackStack,
            enterAnimation,
            exitAnimation,
            popEnterAnimation,
            popExitAnimation
    )
}

fun Fragment.replaceFragmentAnimate(fragment: Fragment,
                                    @IdRes containerViewId: Int,
                                    tag: String = fragment.javaClass.name,
                                    allowStateLoss: Boolean = false) {
    replaceFragment(
            fragment,
            containerViewId,
            tag,
            allowStateLoss,
            enterAnimation = R.anim.slide_in_right,
            exitAnimation = R.anim.slide_out_left,
            popEnterAnimation = R.anim.slide_in_left,
            popExitAnimation = R.anim.slide_out_right
    )
}

fun Fragment.popBackStack() {
    fragmentManager?.popBackStack()
}
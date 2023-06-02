package com.example.qComics.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.qComics.data.utils.OnBackPressed
import com.example.q_comics.R

open class BaseActivity(private var containerId: Int) : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    open fun addFragment(fragment: Fragment?, closeAll: Boolean) {
        supportFragmentManager.run {
            if (backStackEntryCount > 0) popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        addFragment(fragment)
    }

    open fun addFragment(fragment: Fragment?) {
        fragment?.let { addFragment(it, containerId) }
    }

    open fun addFragment(fragment: Fragment, containerId: Int) {
        getCurrentFragment(containerId)?.onPause()

        val tag = fragment.javaClass.name
        supportFragmentManager.beginTransaction().also {
            it.setCustomAnimations(
                    R.anim.anim_translate_in, R.anim.anim_translate_out,
                    R.anim.anim_translate_in, R.anim.anim_translate_out)
            it.add(containerId, fragment, tag)
            it.addToBackStack(tag)
            try {
                it.commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        supportFragmentManager.run {
            addOnBackStackChangedListener { findFragmentById(containerId)?.onResume() }
        }
    }

    open fun replaceFragment(fragment: Fragment?) {
        replaceFragment(fragment, containerId)
    }

    open fun replaceFragment(fragment: Fragment?, containerId: Int) {
        if (fragment == null) {
            supportFragmentManager.run {
                if (backStackEntryCount > 0) popBackStackImmediate()
            }
            return
        }
        getCurrentFragment(containerId)?.let {
            if (fragment.tag == it.tag) return
        }
        val transaction = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.name

        transaction.replace(containerId, fragment, tag)
        try {
            transaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    open fun closeAllFragmentsFromStack() {
        val fm: FragmentManager = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    private fun getCurrentFragment(containerId: Int) =
            supportFragmentManager.findFragmentById(containerId)

    override fun onBackPressed() {
        (getCurrentFragment(containerId) as? OnBackPressed)?.onBackPressed().let {
            if (it == null || it == true) super.onBackPressed()
        }
    }

}
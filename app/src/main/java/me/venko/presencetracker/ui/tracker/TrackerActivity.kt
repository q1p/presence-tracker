package me.venko.presencetracker.ui.tracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.venko.presencetracker.R
import me.venko.presencetracker.utils.replaceFragment

/**
 * @author Victor Kosenko
 */
class TrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        if (savedInstanceState == null) {
            replaceFragment(TrackerFragment.newInstance(), R.id.container, skipBackStack = true)
        }
    }

}

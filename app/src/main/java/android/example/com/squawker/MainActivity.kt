/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.example.com.squawker

import android.content.Intent
import android.database.Cursor
import android.example.com.squawker.following.FollowingPreferenceActivity
import android.example.com.squawker.provider.SquawkContract
import android.example.com.squawker.provider.SquawkProvider
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit internal var mRecyclerView: RecyclerView
    lateinit internal var mLayoutManager: LinearLayoutManager
    lateinit internal var mAdapter: SquawkAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.squawks_recycler_view) as RecyclerView

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        // Use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        // Add dividers
        val dividerItemDecoration = DividerItemDecoration(
                mRecyclerView.context,
                mLayoutManager.orientation)
        mRecyclerView.addItemDecoration(dividerItemDecoration)

        // Specify an adapter
        mAdapter = SquawkAdapter()
        mRecyclerView.adapter = mAdapter

        // Start the loader
        supportLoaderManager.initLoader(LOADER_ID_MESSAGES, null, this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_following_preferences) {
            // Opens the following activity when the menu icon is pressed
            val startFollowingActivity = Intent(this, FollowingPreferenceActivity::class.java)
            startActivity(startFollowingActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Loader callbacks
     */

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // This method generates a selection off of only the current followers
        val selection = SquawkContract.createSelectionForCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this))
        Log.d(LOG_TAG, "Selection is " + selection)
        return CursorLoader(this, SquawkProvider.SquawkMessages.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, SquawkContract.COLUMN_DATE + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        mAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }

    companion object {

        private val LOG_TAG = MainActivity::class.java.simpleName
        private val LOADER_ID_MESSAGES = 0

        internal val MESSAGES_PROJECTION = arrayOf(
                SquawkContract.COLUMN_AUTHOR,
                SquawkContract.COLUMN_MESSAGE,
                SquawkContract.COLUMN_DATE,
                SquawkContract.COLUMN_AUTHOR_KEY
        )

        internal val COL_NUM_AUTHOR = 0
        internal val COL_NUM_MESSAGE = 1
        internal val COL_NUM_DATE = 2
        internal val COL_NUM_AUTHOR_KEY = 3
    }
}

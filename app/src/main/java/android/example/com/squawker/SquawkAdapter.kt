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

import android.database.Cursor
import android.example.com.squawker.provider.SquawkContract
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Converts cursor data for squawk messages into visible list items in a RecyclerView
 */
class SquawkAdapter : RecyclerView.Adapter<SquawkAdapter.SquawkViewHolder>() {


    private var mData: Cursor? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquawkViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_squawk_list, parent, false)

        val vh = SquawkViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: SquawkViewHolder, position: Int) {
        mData!!.moveToPosition(position)

        val message = mData!!.getString(MainActivity.COL_NUM_MESSAGE)
        val author = mData!!.getString(MainActivity.COL_NUM_AUTHOR)
        val authorKey = mData!!.getString(MainActivity.COL_NUM_AUTHOR_KEY)

        // Get the date for displaying
        val dateMillis = mData!!.getLong(MainActivity.COL_NUM_DATE)
        var date = ""
        val now = System.currentTimeMillis()

        // Change how the date is displayed depending on whether it was written in the last minute,
        // the hour, etc.
        if (now - dateMillis < DAY_MILLIS) {
            if (now - dateMillis < HOUR_MILLIS) {
                val minutes = Math.round(((now - dateMillis) / MINUTE_MILLIS).toFloat()).toLong()
                date = minutes.toString() + "m"
            } else {
                val minutes = Math.round(((now - dateMillis) / HOUR_MILLIS).toFloat()).toLong()
                date = minutes.toString() + "h"
            }
        } else {
            val dateDate = Date(dateMillis)
            date = sDateFormat.format(dateDate)
        }

        // Add a dot to the date string
        date = "\u2022 " + date

        holder.messageTextView.text = message
        holder.authorTextView.text = author
        holder.dateTextView.text = date

        // Choose the correct, and in this case, locally stored asset for the instructor. If there
        // were more users, you'd probably download this as part of the message.
        when (authorKey) {
            SquawkContract.ASSER_KEY -> holder.authorImageView.setImageResource(R.drawable.asser)
            SquawkContract.CEZANNE_KEY -> holder.authorImageView.setImageResource(R.drawable.cezanne)
            SquawkContract.JLIN_KEY -> holder.authorImageView.setImageResource(R.drawable.jlin)
            SquawkContract.LYLA_KEY -> holder.authorImageView.setImageResource(R.drawable.lyla)
            SquawkContract.NIKITA_KEY -> holder.authorImageView.setImageResource(R.drawable.nikita)
            else -> holder.authorImageView.setImageResource(R.drawable.test)
        }
    }

    override fun getItemCount(): Int {
        if (null == mData) return 0
        return mData!!.count
    }

    fun swapCursor(newCursor: Cursor?) {
        mData = newCursor
        notifyDataSetChanged()
    }

    inner class SquawkViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        internal val authorTextView: TextView
        internal val messageTextView: TextView
        internal val dateTextView: TextView
        internal val authorImageView: ImageView

        init {
            authorTextView = layoutView.findViewById(R.id.author_text_view) as TextView
            messageTextView = layoutView.findViewById(R.id.message_text_view) as TextView
            dateTextView = layoutView.findViewById(R.id.date_text_view) as TextView
            authorImageView = layoutView.findViewById(R.id.author_image_view) as ImageView
        }
    }

    companion object {
        private val sDateFormat = SimpleDateFormat("dd MMM")


        private val MINUTE_MILLIS = (1000 * 60).toLong()
        private val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private val DAY_MILLIS = 24 * HOUR_MILLIS
    }
}

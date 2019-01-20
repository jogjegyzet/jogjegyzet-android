package com.gergelydaniel.jogjegyzet.ui.document

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gergelydaniel.jogjegyzet.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_comment.view.*

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.text_name
    val comment: TextView = view.text_comment
    val profilePic: ImageView = view.profile_pic
}

class CommentAdapter : RecyclerView.Adapter<ViewHolder>() {
    var data: List<CommentViewModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = this.data[position]

        holder.comment.text = data.comment.message

        if (data.user != null) {
            holder.name.text = data.user.name

            Picasso.get()
                    .load(data.user.portrait.medium)
                    .into(holder.profilePic)
        }
    }

}
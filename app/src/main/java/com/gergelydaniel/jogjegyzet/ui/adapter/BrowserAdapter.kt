package com.gergelydaniel.jogjegyzet.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.DocumentData
import com.gergelydaniel.jogjegyzet.util.Either
import io.reactivex.Observable
import kotlinx.android.synthetic.main.row_category.view.*
import kotlinx.android.synthetic.main.row_document.view.*

open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
internal class CategoryViewHolder(view: View) : ViewHolder(view) {
    val name : TextView = view.category_name
}
internal class DocumentViewHolder(view: View) : ViewHolder(view) {
    val name : TextView = view.document_name
}

private const val TYPE_CATEGORY = 1
private const val TYPE_DOCUMENT = 2

class BrowserAdapter : RecyclerView.Adapter<ViewHolder>() {
    var onClickListener : ((Either<Category, DocumentData>) -> Unit)? = null

    var data: List<Either<Category, DocumentData>> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemViewType(position: Int): Int {
        return if(data[position] is Either.Left) TYPE_CATEGORY else TYPE_DOCUMENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType) {
            TYPE_CATEGORY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false)
                CategoryViewHolder(view)
            }
            TYPE_DOCUMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_document, parent, false)
                DocumentViewHolder(view)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = this.data[position]

        holder.itemView.setOnClickListener {
            onClickListener?.invoke(data)
        }

        when(data) {
            is Either.Left -> {
                holder as CategoryViewHolder
                holder.name.text = data.value.name
            }
            is Either.Right -> {
                holder as DocumentViewHolder
                holder.name.text = data.value.document.name
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder?) {
        super.onViewRecycled(holder)
        holder?.itemView?.setOnClickListener(null)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}


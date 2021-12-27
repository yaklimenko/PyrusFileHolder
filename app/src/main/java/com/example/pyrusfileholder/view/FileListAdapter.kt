package com.example.pyrusfileholder.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pyrusfileholder.R
import com.example.pyrusfileholder.model.FileWrapper

class FileListAdapter : RecyclerView.Adapter<FileViewHolder>() {

    private var files: List<FileWrapper>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newFiles: List<FileWrapper>) {
        files = newFiles
        notifyDataSetChanged()
    }

    fun getFileWrapperByPosition(pos: Int) =
        files?.get(pos)


    fun updateData(newFiles: List<FileWrapper>) {
        if (files == null) {
            setData(newFiles)
            return
        }
        val diffCallback = DiffCallback(files!!, newFiles)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        files = newFiles
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_file, parent, false)
        return FileViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return files?.size ?: 0
    }
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.nameText.text = files?.get(position)?.name
    }

    fun updateDataItem(fileWrapper: FileWrapper) {
            files?.let {
                val idx = it.indexOf(fileWrapper)
                if (idx > -1) {
                    notifyItemChanged(idx)
                }
            }
    }
}

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var nameText: TextView = itemView.findViewById(R.id.fileName)
}

private class DiffCallback(
    private val oldFiles: List<FileWrapper>,
    private val newFiles: List<FileWrapper>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldFiles.size

    override fun getNewListSize() = newFiles.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFiles[oldItemPosition] == newFiles[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFiles[oldItemPosition] == newFiles[newItemPosition]
    }
}


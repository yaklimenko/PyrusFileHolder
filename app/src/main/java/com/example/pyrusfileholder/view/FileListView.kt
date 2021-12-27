package com.example.pyrusfileholder.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pyrusfileholder.databinding.FragmentFileListBinding
import com.example.pyrusfileholder.model.FileWrapper
import com.google.android.material.snackbar.Snackbar

import io.reactivex.rxjava3.subjects.PublishSubject

interface FileListView {
    val actionButtonClicks: PublishSubject<View>
    val fileRemoveSubject: PublishSubject<FileWrapper>
    fun showSnackBar(text: String)
    fun updateList(fileList: List<FileWrapper>)
    fun showList(show: Boolean)
    fun showPlaceholder(show: Boolean)
    fun updateListItemState(fileWrapper: FileWrapper)
}

class FileListViewImpl (private val binding: FragmentFileListBinding): FileListView {

    override val actionButtonClicks: PublishSubject<View> = PublishSubject.create()
    override val fileRemoveSubject: PublishSubject<FileWrapper> = PublishSubject.create()
    private val fileList: RecyclerView = binding.fileListContent.fileList
    private val placeholder: TextView = binding.fileListContent.placeholder
    private val fileAdapter = FileListAdapter()

    init{
        binding.fab.setOnClickListener { v -> actionButtonClicks.onNext(v) }

        fileList.layoutManager = LinearLayoutManager(fileList.context)
        fileList.adapter = fileAdapter

        val itemTouchHelper = ItemTouchHelper(FileItemSwipe(fileAdapter, fileRemoveSubject))
        itemTouchHelper.attachToRecyclerView(fileList)
    }

    override fun showSnackBar(text: String) {
        Snackbar.make(binding.fab, text, Snackbar.LENGTH_LONG).show()
    }

    override fun updateList(fileList: List<FileWrapper>) {
        fileAdapter.updateData(fileList)
    }

    override fun showList(show: Boolean) {
        fileList.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPlaceholder(show: Boolean) {
        placeholder.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateListItemState(fileWrapper: FileWrapper) {
        fileAdapter.updateDataItem(fileWrapper)
    }

    //for swipe
    internal class FileItemSwipe (
        private val adapter: FileListAdapter,
        private val fileRemoveSubject: PublishSubject<FileWrapper>,
        ) :
        ItemTouchHelper.SimpleCallback(0, (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.getFileWrapperByPosition(viewHolder.adapterPosition)?.let {
                fileRemoveSubject.onNext(it)
            }
        }
    }


}
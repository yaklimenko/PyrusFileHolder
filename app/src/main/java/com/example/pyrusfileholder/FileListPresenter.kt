package com.example.pyrusfileholder

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import java.io.File
import java.nio.file.FileSystems


interface FileListRouter {
    fun openFileChooser()
}

class FileListPresenter(
    private var contentResolver: ContentResolver
) {

    private lateinit var view: FileListView
    private lateinit var router: FileListRouter

    private val disposables = CompositeDisposable()


    fun onAttach(view: FileListView, router: FileListRouter) {
        this.view = view
        this.router = router


        disposables += view.actionButtonClicks.subscribe {
            router.openFileChooser()
        }
    }

    fun onDestroy() {
        disposables.clear()
    }

    fun handleReceivedUri(fileUri: Uri) {
        FileSystems.getDefault().getPath("files", "access.log");
        getPathFromUri(fileUri)?.let {


            val inputStream = contentResolver.openInputStream(fileUri);
            //Context.getExternalFilesDir(String)  will write to that dir

            val file = File(fileUri.path)
            Log.d("FileListPresenter", "got a file ${file.name} and this file exists: ${file.exists()}")

        }
    }

    private fun getPathFromUri(fileUri: Uri): String? {
        val cursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        return if (cursor == null) {
            fileUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val result = cursor.getString(idx)
            cursor.close()
            result
        }
    }

    fun saveState(outState: Bundle) {

    }


}
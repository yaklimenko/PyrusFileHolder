package com.example.pyrusfileholder

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.pyrusfileholder.model.FileWrapper
import com.example.pyrusfileholder.view.FileListView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File


interface FileListRouter {
    fun openFileChooser()
}

interface FileListPresenter {
    fun onAttach(view: FileListView, router: FileListRouter, savedState: Bundle?)
    fun onDestroy()
    fun handleReceivedUri(fileUri: Uri)
    fun saveState(outState: Bundle)
    fun getAndShowFileList()
}

class FileListPresenterImpl(
    private var fileService: FileService
): FileListPresenter {

    private lateinit var view: FileListView
    private lateinit var router: FileListRouter
    private var files: ArrayList<FileWrapper>? = null

    private val disposables = CompositeDisposable()


    override fun onAttach(view: FileListView, router: FileListRouter, savedState: Bundle?) {
        this.view = view
        this.router = router

        disposables += view.actionButtonClicks.subscribe {
            router.openFileChooser()
        }

        disposables += view.fileRemoveSubject.subscribe {
            deleteFile(it)
        }

        if (savedState == null) {
            getAndShowFileList()
        } else {
            restoreState(savedState)
            showFileList()
        }

    }

    private fun deleteFile(fileWrapper: FileWrapper) {
        fileService.deleteFile(fileWrapper)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result) {
                        view.showSnackBar("deleted file ${fileWrapper.name}")
                        showFileDeleted(fileWrapper)
                    } else {
                        Log.w("FileListPresenter", "Can't delete file", Exception())
                        view.showSnackBar("Can't delete file")
                        view.updateListItemState(fileWrapper)
                    }
                },
                { throwable ->
                    Log.e("FileListPresenter", "Can't delete file", throwable)
                    view.showSnackBar("Can't delete file")
                    view.updateListItemState(fileWrapper)
                }
            )
    }

    private fun showFileDeleted(fileWrapper: FileWrapper) {
        if (files == null) {
            return
        }
        val newList = mutableListOf<FileWrapper>().also { it.addAll(files!!) }
        newList.remove(fileWrapper)
        files = ArrayList(newList)
        showFileList()
    }

    override fun onDestroy() {
        disposables.clear()
    }

    override fun handleReceivedUri(fileUri: Uri) {
        val fileWrapper = fileService.createFileWrapperFromUri(fileUri)
        fileWrapper?.let {
            fileService.copyFileToAppStorage(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { getAndShowFileList() },
                    { throwable ->
                        Log.e("FileListPresenter", "handleReceivedUri: cannot consume file", throwable)
                        if (throwable is FileAlreadyExistsException) {
                            view.showSnackBar("File ${fileWrapper.name} already exists")
                        } else {
                            view.showSnackBar("Problem occurred while copying File ${fileWrapper.name}")
                        }
                    }
                )
        }
    }

    override fun saveState(outState: Bundle) {
        outState.putParcelableArrayList(KEY_FILE_LIST, files)
        //also save list position

    }

    private fun restoreState(state: Bundle) {
        files = state.getParcelableArrayList(KEY_FILE_LIST)
    }

    override fun getAndShowFileList() {
        fileService.getFileList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list -> processFileList(list) },
                { throwable ->
                    Log.e("FileListPresenter", "handleReceivedUri: cannot consume file", throwable)
                    view.showSnackBar("Problem occurred while trying to get file list")
                }
            )
    }

    private fun processFileList(files: List<File>) {
        if (files.isEmpty()) {
            view.showPlaceholder(true)
            view.showList(false)
        } else {
            this.files = ArrayList(
                files.map { FileWrapper(sourceUri = null, name = it.name) }
            )
            showFileList()
        }
    }

    private fun showFileList() {
        if (files == null) {
            return
        }
        val fileWrappers = files!!
        view.updateList(fileWrappers)
        if (fileWrappers.isEmpty()) {
            view.showList(false)
            view.showPlaceholder(true)
        } else {
            view.showList(true)
            view.showPlaceholder(false)
        }
    }

}

private const val KEY_FILE_LIST = "keyFileList"
package com.example.pyrusfileholder

import android.view.View
import com.example.pyrusfileholder.databinding.FragmentFileListBinding
import com.google.android.material.snackbar.Snackbar

import io.reactivex.rxjava3.subjects.PublishSubject

class FileListView (private val binding: FragmentFileListBinding) {

    val actionButtonClicks = PublishSubject.create<View>()

    init{
        binding.fab.setOnClickListener { v -> actionButtonClicks.onNext(v) }
    }

    fun showSnackBar(text: String) {
        Snackbar.make(binding.fab, text, Snackbar.LENGTH_LONG).show()
    }


}
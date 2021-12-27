package com.example.pyrusfileholder.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileWrapper (
    val sourceUri: Uri?,
    val name: String,
    val size: Int? = null,
): Parcelable
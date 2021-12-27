package com.example.pyrusfileholder

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.pyrusfileholder.model.FileWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.*
import java.nio.file.Files
import java.util.stream.Collectors
import javax.inject.Singleton
import kotlin.io.path.Path

@Singleton
class FileService (
    private val contentResolver: ContentResolver,
    context: Context,
) {

    private val appDir = context.filesDir.path

    fun copyFileToAppStorage(fileWrapper: FileWrapper) : Completable {
        return Completable.create { emitter ->
            var cancelled = false
            var inStr: InputStream? = null
            emitter.setCancellable {
                inStr?.close()
                cancelled = true
            }

            val targetPath = Path("$appDir/${fileWrapper.name}")
            if (fileWrapper.sourceUri == null) {
                emitter.onError(IllegalStateException("no Uri"))
                return@create
            }
            try {
                inStr = contentResolver.openInputStream(fileWrapper.sourceUri)

                inStr?.let { it ->
                    Files.copy(it, targetPath)
                    emitter.onComplete()
                } ?: emitter.onError(IllegalStateException("Cannot take inputStream from source"))
            } catch (e: Exception) {
                if (!cancelled) {
                    emitter.onError(e)
                }
            }
        }
    }

    fun createFileWrapperFromUri(uri: Uri): FileWrapper? {

        val cursor: Cursor = contentResolver.query(
            uri, null, null, null, null, null) ?: return null

        // moveToFirst() returns false if the cursor has 0 rows. Very handy for
        // "if there's anything to look at, look at it" conditionals.
        if (cursor.moveToFirst()) {

            // Note it's called "Display Name". This is
            // provider-specific, and might not necessarily be the file name.
            val displayName: String =
                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            Log.i("FileService", "Display Name: $displayName")

            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            // If the size is unknown, the value stored is null. But because an
            // int can't be null, the behavior is implementation-specific,
            // and unpredictable. So as
            // a rule, check if it's null before assigning to an int. This will
            // happen often: The storage API allows for remote files, whose
            // size might not be locally known.
            val size: String = if (!cursor.isNull(sizeIndex)) {
                // Technically the column stores an int, but cursor.getString()
                // will do the conversion automatically.
                cursor.getString(sizeIndex)
            } else {
                "Unknown"
            }
            Log.i("FileService", "Size: $size, name: $displayName")

            return FileWrapper(uri, displayName, Integer.parseInt(size))
        } else {
            return null
        }
    }

    fun getFileList(): Single<List<File>> {
        return Single.create { emitter ->
            emitter.onSuccess(
                Files.list(Path(appDir)) //here is not RX - this is java streams!
                    .map { it.toFile() }
                    .filter {  file -> file.isFile }
                    .collect(Collectors.toList()))
        }
    }

    fun deleteFile(file: FileWrapper): Single<Boolean> {
        return Single.create { emitter ->
            val filePath = Path("$appDir/${file.name}")
            try {
                emitter.onSuccess(filePath.toFile().delete())
            } catch (t: Throwable) {
                emitter.onError(Exception("cannot delete file", t))
            }
        }
    }
}
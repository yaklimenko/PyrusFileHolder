package com.example.pyrusfileholder.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.example.pyrusfileholder.FileListPresenter
import com.example.pyrusfileholder.FileListPresenterImpl
import com.example.pyrusfileholder.FileService
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: Application) {
    @Provides
    fun provideAppContext(): Context = app

    @Provides
    fun provideContentResolver(): ContentResolver = app.contentResolver

    @Provides
    fun provideFileService(contentResolver: ContentResolver): FileService {
        return FileService(contentResolver, app)
    }
}

@Module(includes = [AppModule::class])
class FileListModule {

    @Provides
    fun provideFileListPresenter(fileService: FileService): FileListPresenter {
        return FileListPresenterImpl(fileService)
    }
}
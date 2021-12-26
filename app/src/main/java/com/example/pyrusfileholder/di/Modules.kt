package com.example.pyrusfileholder.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.example.pyrusfileholder.FileListPresenter
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: Application) {
    @Provides
    fun providesAppContext(): Context = app

    @Provides
    fun providesContentResolver(): ContentResolver = app.contentResolver
}

@Module(includes = [AppModule::class])
class FileListModule {
    @Provides
    fun provideFileListPresenter(contentResolver: ContentResolver): FileListPresenter {
        return FileListPresenter(contentResolver)
    }
}
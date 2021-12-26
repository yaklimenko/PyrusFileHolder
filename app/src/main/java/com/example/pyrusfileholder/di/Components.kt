package com.example.pyrusfileholder.di

import android.content.Context
import com.example.pyrusfileholder.FileListFragment
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun getAppContext(): Context
}

@Component(modules = [FileListModule::class], dependencies = [AppComponent::class])
interface FileHolderComponent {
    fun inject(mainFragment: FileListFragment)
}

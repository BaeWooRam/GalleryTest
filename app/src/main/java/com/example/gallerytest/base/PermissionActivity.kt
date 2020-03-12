package com.example.gallerytest.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.gallerytest.util.AppPermission

abstract class PermissionActivity(@LayoutRes layout:Int):AppCompatActivity(layout) {
    private val tag = javaClass.simpleName
    protected val permission = AppPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permission
            .target(this)
            ?.requestPermission(getCheckPermission())
            ?.excute()
    }

    abstract fun getCheckPermission():Array<String>
}
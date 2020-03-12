package com.example.gallerytest.util

import android.app.Activity
import androidx.fragment.app.Fragment

interface Permission {
    interface Target {
        fun target(targetActivity: Activity?): Request?
        fun target(targetFragment: Fragment?): Request?
    }

    interface Request {
        fun requestPermission(requestPermission: Array<String>?): Permission?
    }

    fun excute()
}
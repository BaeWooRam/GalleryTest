package com.example.gallerytest.util

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * 날짜 : 2019-09-23
 * 작성자 : 배우람
 * 기능 : Permission 기본 필요 요소
 */
abstract class BasePermission : Permission.Target,
    Permission.Request,
    Permission {
    protected var requestPermission: Array<String>? = null
    protected var targetActivity: Activity? = null
    protected var targetFragment: Fragment? = null

    override fun target(targetActivity: Activity?): Permission.Request? {
        this.targetActivity = targetActivity
        return this
    }

    override fun target(targetFragment: Fragment?): Permission.Request? {
        this.targetFragment = targetFragment
        return this
    }

    override fun requestPermission(requestPermission: Array<String>?): Permission? {
        this.requestPermission = requestPermission
        return this
    }

}
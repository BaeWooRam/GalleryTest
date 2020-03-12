package com.example.gallerytest.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList

/**
 * 날짜 : 2019-09-23
 * 작성자 : 배우람
 * 기능 : Permission 기능
 */
class AppPermission : BasePermission() {
    val CHECK_PERMISSION = 100
    private val TAG = javaClass.simpleName
    private lateinit var requiredPermissions : Array<String>
    var isGrantPermission = false

    /**
     * 요청한 Permission 체크 이후 요청실행(Activity)
     */
    private fun checkAndRequestActivtiyPermission() {
        requiredPermissions = getGrantedPermissionList()
        isGrantPermission = checkRequiredPermission()

        if (!isGrantPermission){
            if(!targetActivity!!.isDestroyed){
                ActivityCompat.requestPermissions(targetActivity!!, requiredPermissions, CHECK_PERMISSION)
            }else
                Log.e(TAG, "Activity is Destroyed")
        }
    }

    /**
     * 요청한 Permission 체크 이후 요청실행(Fragment)
     */
    private fun checkAndRequestFragmentPermission() {
        requiredPermissions = getGrantedPermissionList()
        isGrantPermission = checkRequiredPermission()

        if (!isGrantPermission) {
            if(targetFragment!!.isAdded){
                targetFragment!!.requestPermissions(requiredPermissions, CHECK_PERMISSION)
            }else
                Log.e(TAG, "Fragment is not added")
        }
    }


    /**
     * 아직 필요한 Permission 중 승인되지 않은 목록을 가져온다.
     */
    private fun getGrantedPermissionList(): Array<String> {
        val requiredPermissions = ArrayList<String>()
        val context = getTargetContext()!!

        for (permission in requestPermission!!) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission)
            }
        }
        return requiredPermissions.toTypedArray()
    }


    /**
     * 사용자가 요청한 Permission 확인
     */
    private fun checkRequestPermission(): Boolean {
        return if (requestPermission.isNullOrEmpty()) {
            Log.e(TAG, "RequestPermission is NullOrEmpty")
            false
        } else
            true
    }

    /**
     * 사용자가 필요한 요청한 Permission 확인
     */
    private fun checkRequiredPermission(): Boolean {
        return requiredPermissions.isNullOrEmpty()
    }

    private fun getTargetContext(): Context? {
        return when {
            targetActivity != null -> targetActivity!!.baseContext
            targetFragment != null -> targetFragment!!.context
            else -> null
        }
    }

    /**
     * 요청한 Permission requestCode 비교
     * ex) permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)
     *
     * @param resultRequestCode onRequestPermissionsResult -> requestCode
     */
    fun isRequestCode(resultRequestCode: Int): Boolean {
        return resultRequestCode == CHECK_PERMISSION
    }

    /**
     * 요청한 Permission Grant 결과확인
     * ex) permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)
     *
     * @param grantResults onRequestPermissionsResult -> grantResults
     */
    fun isGrantResults(grantResults: IntArray): Boolean {
        for (permission in grantResults) {
            if (permission == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    override fun excute() {
        if (checkRequestPermission())
            when {
                targetActivity != null -> checkAndRequestActivtiyPermission()
                targetFragment != null -> checkAndRequestFragmentPermission()
                else -> Log.e(TAG, "Activity and Fragment is Null")
            }
    }
}
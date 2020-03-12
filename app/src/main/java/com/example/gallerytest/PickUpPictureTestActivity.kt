package com.example.gallerytest

import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import com.example.gallerytest.base.PermissionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PickUpPictureTestActivity : PermissionActivity(R.layout.activity_main) {
    override fun getCheckPermission(): Array<String> {
        return arrayOf("android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)) {
            GlobalScope.async {

            }
        }else {
            finish()
        }

    }


}
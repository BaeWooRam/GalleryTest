package com.example.gallerytest

import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import com.example.gallerytest.base.PermissionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class WriteMediaStoreTestActivity : PermissionActivity(R.layout.activity_main) {

    override fun getCheckPermission(): Array<String> {
        return arrayOf("android.permission.READ_EXTERNAL_STORAGE")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)) {
            GlobalScope.async {

            }
        }else {
            finish()
        }

    }

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "my_image_q.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val collection = MediaStore.Images.Media
        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    val item = contentResolver.insert(collection, values)!!

    private fun writeImageFile(bitmap: Bitmap) {
        val item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        contentResolver.openFileDescriptor(item, "w", null).use {
            // write something to OutputStream
            FileOutputStream(it!!.fileDescriptor).use { outputStream ->

                outputStream.close()
            }
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(item, values, null, null)
    }
}


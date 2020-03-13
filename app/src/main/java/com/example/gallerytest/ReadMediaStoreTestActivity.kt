package com.example.gallerytest

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.gallerytest.base.PermissionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReadMediaStoreTestActivity : PermissionActivity(R.layout.activity_main) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN
    )

    val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
    val selectionArgs = arrayOf(
        dateToTimestamp(day = 22, month = 10, year = 2008).toString()
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

    override fun getCheckPermission(): Array<String> {
        return arrayOf("android.permission.READ_EXTERNAL_STORAGE")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(permission.isGrantPermission)
            GlobalScope.async {
                getImage()
                getAlbumNames()
            }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)) {
            GlobalScope.async {
                getImage()
                getAlbumNames()
            }
        }else {
            finish()
        }

    }

    private suspend fun getImage(){
        withContext(Dispatchers.IO) {
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, // selection
                null, //selectionArgs
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val displayNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val dateTaken = Date(it.getLong(dateTakenColumn))
                    val displayName = it.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    Log.d("Image", "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                    )

                }
            }
        }
    }

    private suspend fun getAlbumNames(){
        withContext(Dispatchers.IO) {
            val projection =
                arrayOf("DISTINCT " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )

            cursor?.use {
                if (it!!.moveToFirst()) {
                    var bucket: String?
                    val bucketColumn =
                        it.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                    do {
                        bucket = it.getString(bucketColumn)
                        Log.e("folderName", bucket)
                    } while (it.moveToNext())
                }
            }
        }
    }

    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            formatter.parse("$day.$month.$year")?.time ?: 0
        }
}


/*
var photoList: ArrayList<PictureItem> = ArrayList<PictureItem>()
var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
var projection = arrayOf(
    MediaStore.Images.Media.DATA,
    MediaStore.Images.Media.DATE_ADDED
)
var cursor: Cursor = getContentResolver().query(uri, projection, null, null, null)
var columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
while (cursor.moveToNext()){
    val folderName: String = StringUtils.getLastFolderName(cursor.getString(columnIndexData))
    if (folderListMap.get(folderName) == null) {
        folderListMap.put(folderName, 0)
    }
    folderListMap.put(folderName, folderListMap.get(folderName) + 1)
    folderListMap.put(
        getString(R.string.activity_picture_pick_ui_designation_allfolder),
        folderListMap.get(getString(R.string.activity_picture_pick_ui_designation_allfolder)) + 1
    )
    val pictureItem = PictureItem(cursor.getString(columnIndexData), false, 0)
    pictureItem.setLastFolderName(folderName)
    photoList.add(pictureItem)
}

cursor.close()
return photoList*/

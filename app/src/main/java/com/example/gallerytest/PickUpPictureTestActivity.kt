package com.example.gallerytest

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.gallerytest.base.PermissionActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


class PickUpPictureTestActivity : PermissionActivity(R.layout.activity_main) {
    private var _imgUri: Uri? = null
    private var _currentPhotoPath: String = ""
    private val FROM_CAMERA = 0
    private val REQUEST_TAKE_PHOTO = 1

    val values = ContentValues().apply {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        put(MediaStore.Images.Media.TITLE, timeStamp);
        put(MediaStore.Images.Media.DISPLAY_NAME, "my_image_q.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    override fun getCheckPermission(): Array<String> {
        return arrayOf(
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permission.isGrantPermission)
//            takePhoto()
            dispatchTakePictureIntent()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permission.isRequestCode(requestCode) && permission.isGrantResults(grantResults)) {
            GlobalScope.async {
                                takePhoto()
//                dispatchTakePictureIntent()
            }
        } else {
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            FROM_CAMERA -> {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                Log.d("FROM_CAMERA", imageBitmap.toString())
                image.setImageBitmap(imageBitmap)
            }

            REQUEST_TAKE_PHOTO -> {
                Log.d("REQUEST_TAKE_PHOTO", "내부 저장소에 성공적으로 저장되었습니다.")
                galleryAddPic()
//                setPic()
            }
        }
    }

    /**
     * 카메라 찍는 Intent 보낸다.
     */
    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, FROM_CAMERA)
                }
            }
    }



    /**
     * 카메라 찍고 파일로 내부 저장소에 저장한다.
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createTempImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("createImageFile", "Error Create File")
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        baseContext,
                        "com.example.gallerytest",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    /**
     * 파일 생성.
     */
    @Throws(IOException::class)
    private fun createTempImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            _currentPhotoPath = absolutePath
        }
    }


    /**
     * 갤러리에 사진 추가
     */
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val file = File(_currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = image.width
        val targetH: Int = image.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(_currentPhotoPath, bmOptions)?.also { bitmap ->
            image.setImageBitmap(bitmap)
        }
    }
}
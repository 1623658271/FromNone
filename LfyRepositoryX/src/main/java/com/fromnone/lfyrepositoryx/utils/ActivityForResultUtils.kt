package com.example.fromnone.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import java.io.File

/**
 * description ： ActivityForResult工具类
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/9 11:01
 */
class ActivityForResultUtils(val activity: AppCompatActivity) {
    //裁剪图片的启动器
    private lateinit var cropPictureLauncher: ActivityResultLauncher<Uri>

    //照相并保存到相册的启动器
    private lateinit var takePhotoAndSaveLauncher: ActivityResultLauncher<Unit?>

    //相册选择的启动器
    private lateinit var selectPhotoLauncher: ActivityResultLauncher<Unit?>

    //照相但不保存的启动器
    private lateinit var takePictureLauncher: ActivityResultLauncher<Void>

    //拍视频并保存的启动器
    private lateinit var videoLauncher: ActivityResultLauncher<Uri>

    //请求单个权限的启动器
    private lateinit var requestLauncher: ActivityResultLauncher<String>

    //请求多个权限的启动器
    private lateinit var requestsLauncher: ActivityResultLauncher<Array<String>>

    private val TAG = "lfy"

    /**
     * 创建相册选择启动器
     */
    fun createSelectPhotoLauncher(selectPhotoCallback: ActivityResultCallback<Uri?>): ActivityForResultUtils {
        selectPhotoLauncher = activity
            .registerForActivityResult(
                SelectPhotoContract(),
                selectPhotoCallback
            )
        return this
    }

    /**
     * 从相册选择照片
     */
    fun selectPhoto() {
        selectPhotoLauncher.launch(null)
    }


    /**
     * 创建拍照的启动器
     */
    fun createTakePhotoLauncher(takePictureCallback: ActivityResultCallback<Bitmap>): ActivityForResultUtils {
        takePictureLauncher = activity
            .registerForActivityResult(
                ActivityResultContracts.TakePicturePreview(),
                takePictureCallback
            )
        return this
    }

    /**
     * 拍照，但不保存到相册
     */
    fun takePhoto() {
        takePictureLauncher.launch(null)
    }


    /**
     * 创建拍照并保存到相册的启动器
     */
    fun createTakePhotoAndSaveLauncher(takeAndSavePhotoCallback: ActivityResultCallback<Uri?>): ActivityForResultUtils {
        takePhotoAndSaveLauncher = activity
            .registerForActivityResult(
                TakePhotoAndSaveContract(),
                takeAndSavePhotoCallback
            )
        return this
    }

    /**
     * 开始拍照并保存到相册
     */
    fun takePhotoAndSave() {
        takePhotoAndSaveLauncher.launch(null)
    }


    /**
     * 创建裁剪图片的启动器
     */
    fun createCropPictureLauncher(cropPictureCallback: ActivityResultCallback<Uri?>): ActivityForResultUtils {
        cropPictureLauncher = activity
            .registerForActivityResult(
                CropPhotoContract(),
                cropPictureCallback
            )
        return this
    }

    /**
     * 开始裁剪图片，传入原始照片uri
     */
    fun cropPicture(uri: Uri) {
        cropPictureLauncher.launch(uri)
    }

    /**
     * 创建 拍视频并保存到相册 的启动器
     */
    fun createTakeVideoLauncher(videoCallback: ActivityResultCallback<Bitmap>): ActivityForResultUtils {
        videoLauncher = activity
            .registerForActivityResult(
                ActivityResultContracts.TakeVideo(),
                videoCallback
            )
        return this
    }

    /**
     * 拍摄视频并保存到相册
     */
    fun takeVideoAndSave() {
        val uri: Uri?
        val mimeType = "video"
        val fileName = "IMG_${System.currentTimeMillis()}.mp4"
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上获取视频uri
            val values = contentValuesOf(
                Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            )
            activity.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            // Android 9 及以下获取视频uri
            FileProvider.getUriForFile(
                activity, "${activity.packageName}.fileprovider",
                File(activity.externalCacheDir, "/$fileName")
            )
        }
        videoLauncher.launch(uri)

    }

    /**
     * 创建单个权限请求启动器
     */
    fun createRequestPermissionLauncher(requestCallback: ActivityResultCallback<Boolean>): ActivityForResultUtils {
        requestLauncher = activity
            .registerForActivityResult(
                ActivityResultContracts.RequestPermission(),
                requestCallback
            )
        return this
    }

    /**
     * 获取单个权限，传入权限名称
     */
    fun requestPermission(permissionName: String) {
        requestLauncher.launch(permissionName)
    }


    /**
     * 创建多个请求启动器
     */
    fun createRequestPermissionsLauncher(requestsCallback: ActivityResultCallback<Map<String, Boolean>>): ActivityForResultUtils {
        requestsLauncher = activity
            .registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                requestsCallback
            )
        return this
    }

    /**
     * 获取多个权限，传入权限名称
     */
    fun requestPermissions(permissionNames: Array<String>) {
        requestsLauncher.launch(permissionNames)
    }


    /**
     * 选择照片的协定
     */
    inner class SelectPhotoContract : ActivityResultContract<Unit?, Uri?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(Intent.ACTION_PICK).setType("image/*")
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    /**
     * 拍照并保存的协定
     */
    inner class TakePhotoAndSaveContract : ActivityResultContract<Unit?, Uri?>() {
        private var uri: Uri? = null

        override fun createIntent(context: Context, input: Unit?): Intent {
            val mimeType = "image/jpeg"
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 及以上获取图片uri
                val values = contentValuesOf(
                    Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                    Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                    Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                )
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                // Android 9 及以下获取图片uri
                FileProvider.getUriForFile(
                    context, "${context.packageName}.provider",
                    File(context.externalCacheDir, "/$fileName")
                )
            }
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) return uri
            return null
        }
    }


    /**
     * 裁剪照片的协定
     */
    inner class CropPhotoContract : ActivityResultContract<Uri, Uri?>() {
        private var output: CropOutput? = null

        override fun createIntent(context: Context, input: Uri): Intent {
            // 获取输入图片uri的媒体类型
            val mimeType = context.contentResolver.getType(input)
            // 创建新的图片名称
            val fileName = "IMG_${System.currentTimeMillis()}.${
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            }"
            val outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 及以上获取图片uri
                val values = contentValuesOf(
                    Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                    Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                    Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                )
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                Uri.fromFile(File(context.externalCacheDir!!.absolutePath, fileName))
            }
            output = CropOutput(outputUri!!, fileName)
            return Intent("com.android.camera.action.CROP")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(input, "image/*")
                .putExtra("outputX", 300)
                .putExtra("outputY", 300)
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra("scale", true)
                .putExtra("crop", true)
                .putExtra("return-data", false) // 在小米手机部分机型中 如果直接返回Data给Intent，图片过大的时候会有问题
                .putExtra("noFaceDetection", true)
                .putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) return output?.uri
            return null
        }

        inner class CropOutput(val uri: Uri, val fileName: String) {
            override fun toString(): String {
                return "{ uri: $uri, fileName: $fileName }"
            }
        }
    }
}

//<provider
//android:authorities="com.example.xxx.fileprovider"
//android:name="androidx.core.content.FileProvider"
//android:exported="false"
//android:grantUriPermissions="true">
//<meta-data
//android:name="android.support.File_PROVIDER_PATHS"
//android:resource="@xml/file_paths"/>
//</provider>
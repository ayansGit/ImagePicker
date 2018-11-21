package m.com.imagepickerdemo

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.theartofdev.edmodo.cropper.CropImageView
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import android.R.attr.data
import android.support.v4.app.NotificationCompat.getExtras
import android.graphics.Bitmap
import android.support.v4.content.FileProvider
import java.io.IOException
import android.content.ContentValues
import android.content.Context
import android.os.StrictMode
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CropImageActivity : AppCompatActivity() {

    private var cropImageView : CropImageView ?= null
    private var btnCropImage : Button ?= null
    private var pictureFilePath: String = ""
    private var picUri: Uri ?= null
    private var imageFilePath: String = ""
    private var chooser : Int = 0
    private var croppedImage : Bitmap ?= null
    private var tempImage : File ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContentView(R.layout.activity_crop_image)

        init()
    }

    private fun init() {

        chooser = intent.extras.getInt(CHOOSER,0)
        cropImageView = findViewById<CropImageView>(R.id.CropImageView)
        btnCropImage = findViewById<Button>(R.id.btnCropImage)
        btnCropImage?.setOnClickListener{v -> cropImage()}


        if(chooser == GALLERY){
            pickImageFromGallery()

        }else if(chooser == CAMERA){
            pickImageFromCamera()
        }

    }


    private fun pickImageFromCamera(){
        try {

            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoIntent.putExtra("return-data", true)
            var photoFile: File? = null
            try {
                photoFile = createImageFile(this)
            } catch (ex: IOException) {

            }

            var uri: Uri? = null
            if (photoFile != null) {
                uri = FileProvider.getUriForFile(this, "m.com.imagepickerdemo.fileprovider", photoFile)
                picUri = uri
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            }
            startActivityForResult(takePhotoIntent, 100)

        } catch (e: ActivityNotFoundException) {

            Toast.makeText(applicationContext, "Couldn't open your camera", Toast.LENGTH_SHORT).show()

        }

    }

    private fun pickImageFromGallery(){

        var galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 101)
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        tempImage = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        imageFilePath = tempImage?.absolutePath.toString()
        return tempImage as File?
    }



    private fun cropImage(){

        val cropped = cropImageView?.getCroppedImage(500, 500)
        if (cropped != null) {
            cropImageView?.setImageBitmap(cropped)
            croppedImage = cropped
            returnWithResult()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){

            100 -> {
                if(resultCode == Activity.RESULT_OK){

                    cropImageView?.setImageUriAsync(picUri)
                    /*if(data != null){
                        cropImageView?.setImageUriAsync(picUri)
                    }*/
                }else{
                    returnWithResult()
                }
            }

            101 -> {
                if(resultCode == Activity.RESULT_OK){

                    if(data != null){
                        cropImageView?.setImageUriAsync(data.data)
                    }
                }else{
                    returnWithResult()
                }
            }
        }
    }

    private fun returnWithResult(){
        val returnIntent = Intent()
        tempImage?.delete()
        if(croppedImage != null) {
            val stream = ByteArrayOutputStream()
            croppedImage?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            returnIntent.putExtra("image", byteArray)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }else{
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }


    }
    companion object {
        const val CHOOSER : String = "chooser"
        const val GALLERY : Int = 0
        const val CAMERA : Int = 1
    }

}

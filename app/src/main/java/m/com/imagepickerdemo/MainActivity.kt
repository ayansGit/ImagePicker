package m.com.imagepickerdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.os.StrictMode
import android.graphics.BitmapFactory
import android.graphics.Bitmap





class MainActivity : AppCompatActivity() {

    private val ALL_PERMISSION: Int = 100
    private var btnPickImage : Button ?= null
    private var image : ImageView ?= null
    private var btnPickImageFromGal : Button ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        btnPickImage = findViewById(R.id.btnPickImage)
        btnPickImageFromGal = findViewById(R.id.btnPickImageFromGal)
        image = findViewById(R.id.image)

        btnPickImage?.setOnClickListener{v-> checkPermission(CropImageActivity.CAMERA)}
        btnPickImageFromGal?.setOnClickListener{v -> checkPermission(CropImageActivity.GALLERY)}

    }

    private fun checkPermission(chooser : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.CAMERA),
                        ALL_PERMISSION)


            } else {
                openCropActivity(chooser)
            }
        } else {
            openCropActivity(chooser)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when{
            grantResults[0] != PackageManager.PERMISSION_GRANTED ->{
                Toast.makeText(this, "Read permission not granted",Toast.LENGTH_SHORT).show()
                return
            }
            grantResults[2] != PackageManager.PERMISSION_GRANTED ->{
                Toast.makeText(this, "Write permission not granted",Toast.LENGTH_SHORT).show()
                return
            }
            /*grantResults[3] != PackageManager.PERMISSION_GRANTED ->{
                Toast.makeText(this, "Camera permission not granted",Toast.LENGTH_SHORT).show()
                return
            }*/

            else -> openCropActivity(1)

        }

    }

    private fun openCropActivity(chooser: Int){
        val intent = Intent(this,CropImageActivity::class.java)
        intent.putExtra(CropImageActivity.CHOOSER,chooser)
        startActivityForResult(intent,150)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){

            150 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val byteArray = data?.getByteArrayExtra("image")
                        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                        image?.setImageBitmap(bmp)
                    }
                    Activity.RESULT_CANCELED -> {

                    }
                }
            }
        }
    }

}

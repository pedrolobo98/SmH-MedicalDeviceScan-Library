package com.smh_medicaldevicescan_library

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smhdevlibrary.ObjectDetectionAssistantHelper
import com.example.smhdevlibrary.Utils
import com.example.smhdevlibrary.YuvToRgbConverter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.smh_medicaldevicescan_library.databinding.ActivityCameraAssistantBinding
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

private const val CAMERA_PERMISSION_REQUEST_CODE = 1

class CameraAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraAssistantBinding

    var save = false
    var lastActivity:String = ""
    private var StartTime: Long = 0
    private var mode: Int = 0

    private lateinit var byteArray: ByteArray
    private lateinit var resultList: List<Float>

    private lateinit var bitmapBuffer: Bitmap

    private val detector by lazy {
        ObjectDetectionAssistantHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StartTime = System.currentTimeMillis()

        if (hasCameraPermission()) bindCameraUseCases()
        else requestPermission()
    }
    // checking to see whether user has already granted permission
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        // opening up dialog to ask for camera permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // user granted permissions - we can set up our scanner
            bindCameraUseCases()
        } else {
            // user did not grant permissions - we can't use the camera
            Toast.makeText(this,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // setting up the preview use case
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            // setting up the analysis use case
            val analysisUseCase = ImageAnalysis.Builder()
                .build()

            // define the actual functionality of our analysis use case
            analysisUseCase.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                { imageProxy ->
                    processImageProxy(imageProxy)
                }
            )

            // configure to use the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase)
            } catch (illegalStateException: IllegalStateException) {
                // If the use case has already been bound to another lifecycle or method is not called on main thread.
                Log.e(TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                Log.e(TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra(Utils().homeActivityKey).toString() != null){
            lastActivity = intent.getStringExtra(Utils().homeActivityKey).toString()
            mode = intent.extras?.getInt(Utils().modeSelectionKey)!!
        }
    }

    private fun processImageProxy(
        imageProxy: ImageProxy
    ) {

        imageProxy.image?.let { image ->
            if (!::bitmapBuffer.isInitialized) {
                bitmapBuffer = Bitmap.createBitmap(
                    image.width, image.height, Bitmap.Config.ARGB_8888)
            }
            val converter = YuvToRgbConverter(this)
            converter.yuvToRgb(image, bitmapBuffer)

            if (save){
                imageProxy.image?.close()
                imageProxy.close()
            }else{

                var (numDetections, imageRe) = detector.runObjectDetection(bitmapBuffer, imageProxy.imageInfo.rotationDegrees, mode)
                reportPrediction(numDetections, imageRe)
                imageProxy.image?.close()
                imageProxy.close()
            }
        }
    }

    private fun reportPrediction (detectedList: List<Float>, bitmap: Bitmap){
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        runOnUiThread {
            binding.detectionsScree.setImageBitmap(bitmap)
            binding.cardView.visibility = View.VISIBLE
            binding.txtViewAnalysis.setText("")
            binding.txtViewAnalysis.bringToFront()
            if (detectedList[0] != 0f &&  detectedList[0] != 9f &&  detectedList[0] != 8f){
                binding.textPrediction.visibility = View.INVISIBLE
                if (detectedList[0] == 1f){
                    if (3 < detectedList[1] && detectedList[1] < 200){
                        //save = true
                        binding.btnCapture.visibility = View.VISIBLE
                    }
                    binding.txtViewAnalysis.setText("Glucometer:\n" + (detectedList[1].toFloat()/10).toString())
                    byteArray  = stream.toByteArray()
                    resultList = detectedList
                }else if (detectedList[0] == 2f){
                    if(100f < detectedList[1] && detectedList[1] < 200f
                        && 20f < detectedList[2] && detectedList[2] < 100f && (detectedList[3] == 0f || (20f < detectedList[3] && detectedList[3] < 200f))){
                        //save = true
                        binding.btnCapture.visibility = View.VISIBLE
                    }
                    binding.txtViewAnalysis.setText("Blood Pressure:" + "\nSys:" + detectedList[1].toString()
                            + "\nDia:" + detectedList[2].toString()
                            + "\nPul:" + detectedList[3].toString())
                    byteArray  = stream.toByteArray()
                    resultList = detectedList
                }else if(detectedList[0] == 3f){
                    if(70f < detectedList[2] && detectedList[2] < 101f
                        && 20f < detectedList[1] && detectedList[1] < 200f){
                        //save = true
                        binding.btnCapture.visibility = View.VISIBLE
                    }
                    binding.txtViewAnalysis.setText("Oximeter:" + "\nPul:" + detectedList[1].toString()
                            + "\nSpo2:" + detectedList[2].toString())
                    byteArray  = stream.toByteArray()
                    resultList = detectedList
                }else if(detectedList[0] == 4f){
                    if(300 < detectedList[1] && detectedList[1] < 450){
                        //save = true
                        binding.btnCapture.visibility = View.VISIBLE
                    }
                    binding.txtViewAnalysis.setText("Termometer:\n" + (detectedList[1].toFloat()/10).toString() + "\nCº:" )
                    byteArray  = stream.toByteArray()
                    resultList = detectedList
                }else if (detectedList[0] == 5f ){
                    if(200 < detectedList[1] && detectedList[1] < 1800){
                        //save = true
                        binding.btnCapture.visibility = View.VISIBLE
                    }
                    binding.txtViewAnalysis.setText("Weight Balance:\n" + (detectedList[1].toFloat()/10).toString() + "\nKg:" )
                    byteArray  = stream.toByteArray()
                    resultList = detectedList
                }else{
                    binding.btnCapture.visibility = View.INVISIBLE
                    binding.cardView.visibility = View.INVISIBLE
                    binding.textPrediction.text = "Invalid Analysis "
                    binding.textPrediction.visibility = View.VISIBLE
                    //save = false
                }
            }else if(detectedList[0] == 9f){
                binding.btnCapture.visibility = View.INVISIBLE
                binding.cardView.visibility = View.INVISIBLE
                binding.textPrediction.text = "Wrong Device Selected"
                binding.textPrediction.visibility = View.VISIBLE
                //save = false
            }else if(detectedList[0] == 8f){
                binding.btnCapture.visibility = View.INVISIBLE
                binding.cardView.visibility = View.INVISIBLE
                binding.textPrediction.text = "Bring the device closer"
                binding.textPrediction.visibility = View.VISIBLE
                //save = false
            }else{
                binding.cardView.visibility = View.INVISIBLE
                binding.btnCapture.visibility = View.INVISIBLE
                binding.textPrediction.visibility = View.GONE
                //save = false
            }
        }
    }

    fun takePhoto(view: View){
        save = true
        val lastActivityIntent = Intent(this, Class.forName(lastActivity))
        lastActivityIntent.putExtra(Utils().listOutKey, resultList.toFloatArray())
        lastActivityIntent.putExtra(Utils().pictureOutKey, byteArray)
        lastActivityIntent.putExtra(Utils().timerOutKey, System.currentTimeMillis()-StartTime)
        finish()
        startActivity(lastActivityIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        save = true
        val intent = Intent(this, Class.forName(lastActivity))
        finish()
        startActivity(intent)
    }

    companion object {
        val TAG: String = CameraAssistantActivity::class.java.simpleName
    }
}
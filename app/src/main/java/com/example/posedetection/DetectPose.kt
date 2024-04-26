package com.example.posedetection

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.IOException

class DetectPose : AppCompatActivity() {

    private lateinit var image : ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var choosePhoto : Button
    private lateinit var textView : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detect_pose)

        mediaPlayer = MediaPlayer()

        image = findViewById(R.id.imageView)
        choosePhoto = findViewById(R.id.choose)
        textView = findViewById(R.id.textView)

        choosePhoto.setOnClickListener{
            chooseImage()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        getImage.launch(intent)
    }

    private val getImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data

            if (data != null &&
                data.data != null){

                val imageUri = data.data

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        imageUri
                    )
                }
                catch (e : IOException) {
                    e.printStackTrace()
                }
                // set image in imageView
                image.setImageBitmap(bitmap)
                createImageBitmap()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createImageBitmap(){

        val bitmapImage = InputImage.fromBitmap(bitmap, 0)
        buildPoseDetection().process(bitmapImage)
            .addOnSuccessListener { pose ->
                // Specific PoseLandmarks individually
                val leftShoulder =
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                        ?.position
                val rightShoulder =
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                        ?.position

                val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
                    ?.position

                val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
                    ?.position

                val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
                    ?.position

                textView.text = "LeftShoulder: $leftShoulder\n" +
                        "RightShoulder: $rightShoulder\n" +
                        "LeftEye: $rightEye\n" +
                        "RightEye: $leftEye\n"+
                        "Nose: $nose\n"
            }
            .addOnFailureListener{ pose ->
                Toast.makeText(this, "Something went wrong: $pose",
                    Toast.LENGTH_LONG).show()
            }
    }

    private fun buildPoseDetection(): PoseDetector {

        // detector option
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()

        return PoseDetection.getClient(options)
    }

}

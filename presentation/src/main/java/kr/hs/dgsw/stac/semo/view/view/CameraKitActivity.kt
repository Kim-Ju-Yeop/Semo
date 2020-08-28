package kr.hs.dgsw.stac.semo.view.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_camera_kit.*
import kr.hs.dgsw.stac.semo.base.BaseActivity
import kr.hs.dgsw.stac.semo.databinding.ActivityCameraKitBinding
import kr.hs.dgsw.stac.semo.view.dialog.NextDialog
import kr.hs.dgsw.stac.semo.viewmodel.CameraKitViewModel
import kr.hs.dgsw.stac.semo.widget.tensorflow.Classifier
import kr.hs.dgsw.stac.semo.widget.tensorflow.TensorFlowImageClassifier
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.Executors

class CameraKitActivity : BaseActivity<ActivityCameraKitBinding, CameraKitViewModel>() {

    private lateinit var classifier: Classifier

    private val MODEL_PATH = "model.tflite"
    private val LABEL_PATH = "labels.txt"
    private val INPUT_SIZE = 224
    private val QUANT = false

    private val laundryList = ArrayList<String>()

    override val viewModel: CameraKitViewModel
        get() = getViewModel(CameraKitViewModel::class)

    override fun init() {
        initTensorFlowAndLoadModel()
    }

    override fun observerViewModel() {
        with(viewModel) {
            onDetectEvent.observe(this@CameraKitActivity, Observer {
                cameraKitView.captureImage(object : CameraKitView.ImageCallback {
                    override fun onImage(p0: CameraKitView?, p1: ByteArray?) {
                        var bitmap = BitmapFactory.decodeByteArray(p1, 0, p1!!.size)
                        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)

                        val results = classifier.recognizeImage(bitmap)

                        if (results[0].title == "null") {
                            Toast.makeText(applicationContext, "이미지 인식 실패, 다시 촬영해주세요!", Toast.LENGTH_SHORT).show()
                        } else {
                            laundryList.add(results[0].title)

                            val dialog = NextDialog()
                            dialog.show(supportFragmentManager)
                            dialog.onMoveEvent.observe(this@CameraKitActivity, Observer {
                                // 상세 정보 화면으로 이동하기
                            })
                        }
                    }
                })
            })
        }
    }

    @Throws(Exception::class)
    private fun initTensorFlowAndLoadModel() {
        try {
            classifier = TensorFlowImageClassifier().create(assets, MODEL_PATH, LABEL_PATH, INPUT_SIZE, QUANT)
            makeDetectButtonVisible()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun makeDetectButtonVisible() {
        runOnUiThread { detectButton.visibility = View.VISIBLE }
    }

    override fun onStart() {
        super.onStart()
        cameraKitView.onStart()
    }
    override fun onResume() {
        super.onResume()
        cameraKitView.onResume()
    }
    override fun onPause() {
        super.onPause()
        cameraKitView.onPause()
    }
    override fun onStop() {
        super.onStop()
        cameraKitView.onStop()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
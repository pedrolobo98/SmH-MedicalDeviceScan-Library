package com.example.smhdevlibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class ObjectDetectionHelper(context: Context) {
    val context: Context = context

    /**
     * runObjectDetection(bitmap: Bitmap)
     *      TFLite Object Detection function
     */
    fun runObjectDetection(bitmap: Bitmap, imageRotation: Int, mode: Int): Utils.output {

        val matrix = Matrix().apply {
            postRotate(imageRotation.toFloat())
        }
        val bitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

        // Setup digit classifier.

        // Step 1: Create TFLite's TensorImage object
        val image = TensorImage.fromBitmap(bitmap)

        // Step 2: Initialize the detector object
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(20)
            .setScoreThreshold(0.5f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            context,
            "ecra_15d_metadata.tflite",
            options
        )

        // Step 3: Feed given image to the detector
        val results = detector.detect(image)

        // Step 4: Parse the detection result and show it
        var resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, category.label)
        }

        // Draw the detection result on the bitmap and show it.

        if (resultToDisplay.any { item -> item.text == "15\r" }){
            var screenArea = resultToDisplay.filter { it.text == "15\r"}
            if (screenArea.size == 1 && ((bitmap.width * bitmap.height)/15 <
                        (screenArea[0].boundingBox.right.toInt() - screenArea[0].boundingBox.left.toInt()) *
                        (screenArea[0].boundingBox.bottom.toInt() - screenArea[0].boundingBox.top.toInt()))){
                resultToDisplay = resultToDisplay.filter { it.text != "15\r"}
                var conditionTree = ConditionTree(bitmap, context)
                var BuildList = buildList(bitmap, resultToDisplay)
                var FilteresArea = conditionTree.filterByArea(BuildList)
                var (resizeBoundinBox, imgg, area) = conditionTree.resizeByBoundingBoxLimits(FilteresArea)

                var img = Bitmap.createScaledBitmap(imgg, 320, 320, true)

                if (resizeBoundinBox.size != 0){
                    when(mode) {
                        1 -> return Utils.output(
                            conditionTree.autoDeviceDetection(resizeBoundinBox),
                            img
                        )
                        2 -> return Utils.output(
                            conditionTree.termoDeviceDetection(resizeBoundinBox),
                            img
                        )
                        3 -> return Utils.output(
                            conditionTree.balanceDeviceDetection(
                                resizeBoundinBox
                            ), img
                        )
                        4 -> return Utils.output(
                            conditionTree.glucoDeviceDetection(resizeBoundinBox),
                            img
                        )
                        5 -> return Utils.output(
                            conditionTree.tenseDeviceDetection(resizeBoundinBox),
                            img
                        )
                        else -> return Utils.output(
                            conditionTree.oxiDeviceDetection(
                                resizeBoundinBox
                            ), img
                        )
                    }
                }else{
                    val conf = Bitmap.Config.ARGB_8888 // see other conf types
                    val bmp = Bitmap.createBitmap(320, 320, conf)
                    return Utils.output(mutableListOf(0f, 0f, 0f, 0f, 0f, 0f), bmp)
                }
            }else{
                val conf = Bitmap.Config.ARGB_8888 // see other conf types
                val bmp = Bitmap.createBitmap(320, 320, conf)
                return Utils.output(mutableListOf(8f, 0f, 0f, 0f, 0f, 0f), bmp)
            }
        }else{
            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            val bmp = Bitmap.createBitmap(320, 320, conf)
            return Utils.output(mutableListOf(0f, 0f, 0f, 0f, 0f, 0f), bmp)
        }
        //return conditionTree.autoDeviceDetection(resizeBoundinBox)
    }

    fun buildList(bitmap: Bitmap, detectionResults: List<DetectionResult>): List<Utils.unit> {

        val results = mutableListOf<Utils.unit>()

        detectionResults.forEach {

            val box = it.boundingBox
            var dig = it.text.filterNot { it == "\r"[0] }.toInt()
            if (dig < 10){
                dig = dig + 1
            }else if (dig == 10){
                dig = 0
            }else{
                dig = dig - 1
            }
            if (!(box.left.toInt() < 0 || box.top.toInt() < 0 || box.right.toInt() > bitmap.width || box.bottom.toInt() > bitmap.height)){

                //List to save [area, min. X, min. Y, width, height, center x, center y, digit, cluster, group value]
                results.add(
                    Utils.unit(
                        (box.right.toInt() - box.left.toInt()) * (box.bottom.toInt() - box.top.toInt()),
                        box.left.toInt(),
                        box.top.toInt(),
                        box.right.toInt() - box.left.toInt(),
                        box.bottom.toInt() - box.top.toInt(),
                        (box.right.toInt() - box.left.toInt()) / 2 + box.left.toInt(),
                        (box.bottom.toInt() - box.top.toInt()) / 2 + box.top.toInt(),
                        dig,
                        0
                    )
                )
            }
        }
        return results
    }
}

data class DetectionResult(val boundingBox: RectF, val text: String)
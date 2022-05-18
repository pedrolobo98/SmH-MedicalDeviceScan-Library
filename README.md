[![Maven Central](https://img.shields.io/maven-central/v/io.github.pedrolobo98/SmH-MedicalDeviceScan-Library.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.pedrolobo98%22%20AND%20a:%22SmH-MedicalDeviceScan-Library%22)

# SmH-MedicalDeviceScan-Library
SmH-MedicalDeviceScan-Library is a library that allows you to perform intelligent scanning  through camera live mode of medical analysis displayed on screens of devices such as oximeter, blood pressure meter, glucometer, thermometer and scales.

![Imagem2](https://user-images.githubusercontent.com/57667127/169053195-ded372ef-fa1f-469b-bc86-ce1e588e093e.png)

## Integration
Add de dependencie to the build.gradle file.

```
dependencies {
   implementation 'io.github.pedrolobo98:SmH-MedicalDeviceScan-Library:1.0.0'
}
```
## Usage
This topic will explain how to use the library in question, smart scan of a single image and smart scan through camera live mode with or without assistant.
In the case of smart scan through camera live mode, you need to add the following permission in the manifest file.

```
<uses-permission android:name="android.permission.CAMERA" />
```
### 1. Single image
Start class
```
private val detector by lazy {
    ObjectDetectionAssistantHelper(this)
}
```
Run the following function. This function receives the image (Bitmap) to be processed, the rotation (Int) at which it was acquired as well as the device or devices being analyzed. Device selection is done through an 'int' input variable ranging from 0 to 6.

![Capturar1](https://user-images.githubusercontent.com/57667127/169086536-57f8f143-e9e8-4e56-8c43-280266152e2f.PNG)

```
var (listDetections, bitmapOut) = detector.runObjectDetection(bitmapIn, rotation, mode)
```
This function returns two variables, an image (bitmap) and a list (List<Float>). The image contains the region of the device's screen that contains the detected objects as well as their bounding boxes drawn on the image. The list contains the extracted information which is represented in the following figure.
   
![Capturar3](https://user-images.githubusercontent.com/57667127/169083405-6141a8df-d0f5-461d-b12e-1dbc1c478415.PNG)

### 2. Camera Live Mode Without Assistant
   
Start the activity responsible for camera live mode from your activity.
```
val intent = Intent(this, CameraActivity::class.java)
var mode = 0
intent.putExtra(Utils().modeSelectionKey, mode)
intent.putExtra(Utils().homeActivityKey, this::class.java.name)
finish()
startActivity(intent)   
```
To receive the information extracted in the initial activity
   
```
override fun onResume() {
    super.onResume()
    if (intent.extras?.getByteArray(Utils().pictureOutKey) != null){

        val byteArray = intent.extras?.getByteArray(Utils().pictureOutKey)
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        txtTimer.setText(time.toString())
        inputImageView.setImageBitmap(bmp)
    }
}
   
### 3. Camera Live Mode With
   
Start the activity responsible for camera live mode from your activity.
```
val intent = Intent(this, CameraAssistantActivity::class.java)
var mode = 0
intent.putExtra(Utils().modeSelectionKey, mode)
intent.putExtra(Utils().homeActivityKey, this::class.java.name)
finish()
startActivity(intent)   
```
To receive the information extracted in the initial activity
   
```
override fun onResume() {
    super.onResume()
    if (intent.extras?.getByteArray(Utils().pictureOutKey) != null){

        val byteArray = intent.extras?.getByteArray(Utils().pictureOutKey)
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        txtTimer.setText(time.toString())
        inputImageView.setImageBitmap(bmp)
    }
}

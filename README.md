[![Maven Central](https://img.shields.io/maven-central/v/io.github.pedrolobo98/SmH-MedicalDeviceScan-Library.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.pedrolobo98%22%20AND%20a:%22SmH-MedicalDeviceScan-Library%22)

# SmH-MedicalDeviceScan-Library
SmH-MedicalDeviceScan-Library is a library that allows you to perform intelligent scanning of medical analysis displayed on screens of devices such as oximeter, blood pressure meter, glucometer, thermometer and scales.

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


package com.example.smhmedicaldevicescanlibrary

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.smhdevlibrary.Utils
import com.smh_medicaldevicescan_library.CameraActivity
import com.smh_medicaldevicescan_library.CameraAssistantActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnCam: Button

    private lateinit var inputImageView: ImageView

    private lateinit var lLAnalysis: LinearLayoutCompat

    private lateinit var txtAnalysis1: TextView
    private lateinit var txtAnalysis2: TextView
    private lateinit var txtAnalysis3: TextView
    private lateinit var txtDoc: TextView
    private lateinit var txtTimer: TextView
    private lateinit var editTxtAnalysis1: EditText
    private lateinit var editTxtAnalysis2: EditText
    private lateinit var editTxtAnalysis3: EditText
    private lateinit var spnModeSelection: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputImageView = findViewById(R.id.imageView)
        lLAnalysis = findViewById(R.id.lLConfirmAna)

        txtAnalysis1 = findViewById(R.id.txtAna1)
        txtAnalysis2 = findViewById(R.id.txtAna2)
        txtAnalysis3 = findViewById(R.id.txtAna3)
        txtDoc = findViewById(R.id.textView)
        editTxtAnalysis1 = findViewById(R.id.editTxtAna1)
        editTxtAnalysis2 = findViewById(R.id.editTxtAna2)
        editTxtAnalysis3 = findViewById(R.id.editTxtAna3)
        spnModeSelection = findViewById(R.id.modeselection_spinner)

        txtTimer = findViewById(R.id.viewTimer)



        ArrayAdapter.createFromResource(
            this,
            R.array.scan_modes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModeSelection.adapter = adapter
        }
    }
    fun closeSaveWindow(view: View) {
        inputImageView.setImageResource(0);
        lLAnalysis.setVisibility(View.INVISIBLE)
        txtAnalysis1.setText("")
        editTxtAnalysis1.setText("")
        txtAnalysis2.setText("")
        editTxtAnalysis2.setText("")
        txtAnalysis3.setText("")
        editTxtAnalysis3.setText("")
        txtTimer.setText("")
    }

    fun warning(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_automode, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i -> }
        builder.show()
    }

    fun manualInsertion(view: View){
        if (spnModeSelection.getSelectedItem().toString() == "Auto"){
            Toast.makeText(this, "Select a specific device", Toast.LENGTH_SHORT).show()
        }else if (spnModeSelection.getSelectedItem().toString() == "Termometer"){
            lLAnalysis.setVisibility(View.VISIBLE)
            txtAnalysis1.setText("Cº:")
            txtAnalysis2.setVisibility(View.INVISIBLE)
            editTxtAnalysis2.setVisibility(View.INVISIBLE)
            txtAnalysis3.setVisibility(View.INVISIBLE)
            editTxtAnalysis3.setVisibility(View.INVISIBLE)
        }else if (spnModeSelection.getSelectedItem().toString() == "Weight Balance"){
            lLAnalysis.setVisibility(View.VISIBLE)
            txtAnalysis1.setText("Kg:")
            txtAnalysis2.setVisibility(View.INVISIBLE)
            editTxtAnalysis2.setVisibility(View.INVISIBLE)
            txtAnalysis3.setVisibility(View.INVISIBLE)
            editTxtAnalysis3.setVisibility(View.INVISIBLE)
        }else if (spnModeSelection.getSelectedItem().toString() == "Glucometer"){
            lLAnalysis.setVisibility(View.VISIBLE)
            txtAnalysis1.setText("Gluco:")
            txtAnalysis2.setVisibility(View.INVISIBLE)
            editTxtAnalysis2.setVisibility(View.INVISIBLE)
            txtAnalysis3.setVisibility(View.INVISIBLE)
            editTxtAnalysis3.setVisibility(View.INVISIBLE)
        }else if (spnModeSelection.getSelectedItem().toString() == "Blood Pressure"){
            lLAnalysis.setVisibility(View.VISIBLE)
            txtAnalysis1.setText("Sys:")
            txtAnalysis2.setText("Dia:")
            txtAnalysis3.setText("Pul:")
            txtAnalysis2.setVisibility(View.VISIBLE)
            editTxtAnalysis2.setVisibility(View.VISIBLE)
            txtAnalysis3.setVisibility(View.VISIBLE)
            editTxtAnalysis3.setVisibility(View.VISIBLE)
        }else if (spnModeSelection.getSelectedItem().toString() == "Oximeter"){
            lLAnalysis.setVisibility(View.VISIBLE)
            txtAnalysis1.setText("Pul:")
            txtAnalysis2.setText("Spo2:")
            txtAnalysis2.setVisibility(View.VISIBLE)
            editTxtAnalysis2.setVisibility(View.VISIBLE)
            txtAnalysis3.setVisibility(View.INVISIBLE)
            editTxtAnalysis3.setVisibility(View.INVISIBLE)
        }
    }

    fun openCameraToScan(view: View) {
        // Do something in response to button
        val intent = Intent(this, CameraActivity::class.java)
        var mode = 0
        if (spnModeSelection.getSelectedItem().toString() == "Auto"){
            mode = 1
        }else if (spnModeSelection.getSelectedItem().toString() == "Termometer"){
            mode = 2
        }else if (spnModeSelection.getSelectedItem().toString() == "Weight Balance"){
            mode = 3
        }else if (spnModeSelection.getSelectedItem().toString() == "Glucometer"){
            mode = 4
        }else if (spnModeSelection.getSelectedItem().toString() == "Blood Pressure"){
            mode = 5
        }else if (spnModeSelection.getSelectedItem().toString() == "Oximeter"){
            mode = 6
        }
        intent.putExtra(Utils().modeSelectionKey, mode)
        intent.putExtra(Utils().homeActivityKey, this::class.java.name)
        finish()
        startActivity(intent)
    }

    fun openCameraToScanAssistant(view: View) {
        // Do something in response to button
        val intent = Intent(this, CameraAssistantActivity::class.java)
        var mode = 0
        if (spnModeSelection.getSelectedItem().toString() == "Auto"){
            mode = 1
        }else if (spnModeSelection.getSelectedItem().toString() == "Termometer"){
            mode = 2
        }else if (spnModeSelection.getSelectedItem().toString() == "Weight Balance"){
            mode = 3
        }else if (spnModeSelection.getSelectedItem().toString() == "Glucometer"){
            mode = 4
        }else if (spnModeSelection.getSelectedItem().toString() == "Blood Pressure"){
            mode = 5
        }else if (spnModeSelection.getSelectedItem().toString() == "Oximeter"){
            mode = 6
        }
        intent.putExtra(Utils().modeSelectionKey, mode)
        intent.putExtra(Utils().homeActivityKey, this::class.java.name)
        finish()
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras?.getByteArray(Utils().pictureOutKey) != null){

            val byteArray = intent.extras?.getByteArray(Utils().pictureOutKey)
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

            val time = intent.extras?.getLong(Utils().timerOutKey)
            txtTimer.setText(time.toString())
            inputImageView.setImageBitmap(bmp)
        }
        if (intent.extras?.getFloatArray(Utils().listOutKey) != null){
            var list = intent.extras?.getFloatArray(Utils().listOutKey)
            lLAnalysis.setVisibility(View.VISIBLE)

            if (list?.get(0) == 1f){
                txtAnalysis1.setText("Gluco:")
                editTxtAnalysis1.setText((list?.get(1).toFloat()/10).toString())
                txtAnalysis2.setVisibility(View.INVISIBLE)
                editTxtAnalysis2.setVisibility(View.INVISIBLE)
                txtAnalysis3.setVisibility(View.INVISIBLE)
                editTxtAnalysis3.setVisibility(View.INVISIBLE)
            }else if (list?.get(0) == 2f){
                txtAnalysis1.setText("Sys:")
                editTxtAnalysis1.setText(list?.get(1).toString())
                txtAnalysis2.setText("Dia:")
                editTxtAnalysis2.setText(list?.get(2).toString())
                txtAnalysis3.setText("Pul:")
                editTxtAnalysis3.setText(list?.get(3).toString())
            }else if (list?.get(0) == 3f) {
                txtAnalysis1.setText("Pul:")
                editTxtAnalysis1.setText(list?.get(1).toString())
                txtAnalysis2.setText("Spo2:")
                editTxtAnalysis2.setText(list?.get(2).toString())
                txtAnalysis3.setVisibility(View.INVISIBLE)
                editTxtAnalysis3.setVisibility(View.INVISIBLE)
            }else if (list?.get(0) == 4f) {
                txtAnalysis1.setText("Cº:")
                editTxtAnalysis1.setText((list?.get(1).toFloat()/10).toString())
                txtAnalysis2.setVisibility(View.INVISIBLE)
                editTxtAnalysis2.setVisibility(View.INVISIBLE)
                txtAnalysis3.setVisibility(View.INVISIBLE)
                editTxtAnalysis3.setVisibility(View.INVISIBLE)
            }else if (list?.get(0) == 5f) {
                txtAnalysis1.setText("Kg:")
                editTxtAnalysis1.setText((list?.get(1).toFloat()/10).toString())
                txtAnalysis2.setVisibility(View.INVISIBLE)
                editTxtAnalysis2.setVisibility(View.INVISIBLE)
                txtAnalysis3.setVisibility(View.INVISIBLE)
                editTxtAnalysis3.setVisibility(View.INVISIBLE)
            }
        }
    }
}
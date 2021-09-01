package com.cooper.aws_main

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openLearningModeActivity(view: View) {

        val learningModeActivityIntent = Intent(this, LearningModeActivity::class.java)
        startActivity(learningModeActivityIntent)
    }

    fun openExamModeActivity(view: View) {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.exam_start_dialog_message))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.dialog_start_button_text), DialogInterface.OnClickListener {
                    dialog, id ->

                    val examModeActivityIntent = Intent(this, ExamModeActivity::class.java)
                    startActivity(examModeActivityIntent)
                })
                .setNegativeButton(getString(R.string.dialog_cansel_button_text), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                })
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}
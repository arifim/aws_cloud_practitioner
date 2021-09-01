package com.cooper.aws_main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class ExamResult : AppCompatActivity() {

    private lateinit var totalScore: TextView
    private lateinit var correctAnswers: TextView
    private lateinit var wrongAnswers: TextView
    private lateinit var timeTaken: TextView
    private lateinit var tvCongratText: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_result)

        totalScore = findViewById(R.id.tv_total_score)
        correctAnswers = findViewById(R.id.tv_correct_answers)
        wrongAnswers = findViewById(R.id.tv_wrong_answers)
        timeTaken = findViewById(R.id.tv_time_taken)
        tvCongratText = findViewById(R.id.tv_congret_text)

        val score = intent.getStringExtra("total_score")?.toInt()
        totalScore.text = "$score%"
        correctAnswers.text = intent.getStringExtra("correct")!!.toString()
        wrongAnswers.text = intent.getStringExtra("wrong")!!.toString()
        timeTaken.text = intent.getStringExtra("time_left")!!.toString()

        if (score!! < 70)
            tvCongratText.text = "You didn't pass"
    }

    fun finishButtonClicked(view: View) {
        this.finish()
    }
}
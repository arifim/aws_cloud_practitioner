package com.cooper.aws_main

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.io.InputStream

class ExamModeActivity : AppCompatActivity() {

    private lateinit var questions: Array<Question>
    private lateinit var timer: CountDownTimer
    lateinit var tvTimer: TextView
    private lateinit var tvAnswers: Array<TextView>
    private lateinit var answersBackground: Array<LinearLayout>
    private var userAnswers: MutableList<String> = mutableListOf()
    private var correctAnswers: List<String> = listOf()
    private var currentQuestionIndex = 0
    private lateinit var submitButton: Button
    private lateinit var tvQuestion: TextView
    private lateinit var tvQuestionCount: TextView
    private var score = 0
    private var wrong = 0
    private var timeLeft = 0L
    private var questionNumber = 1
    private lateinit var skippedQuestions: MutableList<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_mode)


        tvQuestion = findViewById(R.id.tv_question)
        tvAnswers = arrayOf(findViewById(R.id.tv_answer0),
                findViewById(R.id.tv_answer1),
                findViewById(R.id.tv_answer2),
                findViewById(R.id.tv_answer3))
        answersBackground = arrayOf(findViewById(R.id.ll_answer0),
                findViewById(R.id.ll_answer1),
                findViewById(R.id.ll_answer2),
                findViewById(R.id.ll_answer3))

        tvQuestionCount = findViewById(R.id.tv_question_count)
        submitButton = findViewById(R.id.submitButton)


        // Timer
        tvTimer = findViewById(R.id.tv_timer)

        timer = object: CountDownTimer(3600000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = 3600000 - millisUntilFinished
                tvTimer.text = millisecondsToReadableFormat(millisUntilFinished)
            }
            override fun onFinish() {

            }
        }
        timer.start()


        // Read question.json file from Raw folder
        val questionsFile: InputStream = resources.openRawResource(R.raw.questions)
        val buffer = ByteArray(questionsFile.available())
        questionsFile.read(buffer)
        questionsFile.close()
        val jsonString = String(buffer)

        // Parse json file
        questions = Gson().fromJson(jsonString, Array<Question>::class.java)

        // Create indexes and shuffle questions
        skippedQuestions = (questions.indices).toMutableList()
        skippedQuestions.shuffle()
        currentQuestionIndex = skippedQuestions.first()
        skippedQuestions.remove(currentQuestionIndex)

        showNextQuestion()

    }

    private fun showNextQuestion() {
        tvQuestion.text = questions[currentQuestionIndex].question
        correctAnswers = questions[currentQuestionIndex].correctAnswers.toList()
        for ( (tvAnswer, answer) in tvAnswers zip questions[currentQuestionIndex].answers) {
            tvAnswer.text = answer
        }

        tvQuestionCount.text = "Question ${questionNumber++} of ${questions.size}"
    }

    private fun clearAllSelections() {

        for ((tv_answer, bg) in tvAnswers zip answersBackground) {
            bg.setBackgroundResource(R.drawable.default_textview)
            tv_answer.setTextColor(Color.parseColor("#656565"))
            tv_answer.typeface = Typeface.DEFAULT
        }

    }


    fun submitButtonCLicked(view: View) {

        clearAllSelections()
        if (skippedQuestions.size == 1)
            submitButton.text = "Finish"

        if (skippedQuestions.size > 0) {
            currentQuestionIndex = skippedQuestions.first() //(currentQuestionIndex + 1) % questions.size
            skippedQuestions.remove(currentQuestionIndex)
            showNextQuestion()


        } else {

            if (checkUserAnswers()) score++ else wrong++ // Check the last question answers

            // Calculating result
            val resultScore = 100 / questions.size * score
            val result = Intent(this, ExamResult::class.java)
            with(result) {
                putExtra("total_score", resultScore.toString())
                putExtra("correct", score.toString())
                putExtra("wrong", wrong.toString())
                putExtra("time_left", millisecondsToReadableFormat(timeLeft))
                startActivity(this)
            }
            finish()
        }
        if (checkUserAnswers()) score++ else wrong++
        userAnswers.clear()
    }


    fun answerSelected(view: View) {


        for ((tv_answer, bg) in tvAnswers zip answersBackground) {
            if (tv_answer.id == view.id) {
                val selectedAnswerLetter = tv_answer.tag.toString()
                if (!userAnswers.contains(selectedAnswerLetter)) {
                    userAnswers.add(selectedAnswerLetter)
                    bg.setBackgroundResource(R.drawable.selected_textview)
                    tv_answer.setTextColor(Color.BLACK)
                    tv_answer.typeface = Typeface.DEFAULT_BOLD
                    break
                }
                else {
                    userAnswers.remove(selectedAnswerLetter)
                    bg.setBackgroundResource(R.drawable.default_textview)
                    tv_answer.setTextColor(Color.parseColor("#656565"))
                    tv_answer.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    private fun checkUserAnswers(): Boolean {

        if (userAnswers.size != correctAnswers.size)
            return false

        for (answer in correctAnswers) {
            if (!userAnswers.contains(answer))
                return false
        }
        return true
    }

    fun skipButtonCLicked(view: View) {
        val elem = currentQuestionIndex
        skippedQuestions.remove(elem)
        skippedQuestions.add(elem)
        currentQuestionIndex = skippedQuestions.first()
        skippedQuestions.remove(currentQuestionIndex)
        showNextQuestion()
    }

    private fun millisecondsToReadableFormat(milliseconds: Long): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        return minutes.toString().padStart(2, '0') + ":" +
                seconds.toString().padStart(2, '0')
    }

}
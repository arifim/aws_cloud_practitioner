package com.cooper.aws_main

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import java.io.InputStream

class LearningModeActivity : AppCompatActivity() {

    private lateinit var questions: Array<Question>
    private lateinit var tvAnswers: Array<TextView>
    private lateinit var answersBackground: Array<LinearLayout>
    private lateinit var tvQuestion: TextView
    private var correctAnswers: List<String> = listOf()
    private var userAnswers: MutableList<String> = mutableListOf()
    private var currentQuestionIndex = 0
    private lateinit var tvQuestionCount: TextView
    private lateinit var nextQuestionButton: Button


    private lateinit var explanationLabel: TextView
    private lateinit var explanationText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_mode)

        tvQuestion = findViewById(R.id.tv_question)
        tvAnswers = arrayOf(findViewById(R.id.tv_answer0),
                            findViewById(R.id.tv_answer1),
                            findViewById(R.id.tv_answer2),
                            findViewById(R.id.tv_answer3))
        answersBackground = arrayOf(findViewById(R.id.ll_answer0),
                findViewById(R.id.ll_answer1),
                findViewById(R.id.ll_answer2),
                findViewById(R.id.ll_answer3))

        explanationLabel = findViewById(R.id.tv_expl_lable)
        explanationText = findViewById(R.id.tv_expl)

        tvQuestionCount = findViewById(R.id.tv_question_count)
        nextQuestionButton = findViewById(R.id.nextButton)
        
        val questionsFile: InputStream = resources.openRawResource(R.raw.questions)
        val buffer = ByteArray(questionsFile.available())
        questionsFile.read(buffer)
        questionsFile.close()
        val jsonString = String(buffer)

        // Parse json file

        questions = Gson().fromJson(jsonString, Array<Question>::class.java)

        //skippedQuestionIndexes = (questions.indices).toMutableList()

        showNextQuestion()
    }

    private fun showNextQuestion() {
        tvQuestion.text = questions[currentQuestionIndex].question
        correctAnswers = questions[currentQuestionIndex].correctAnswers.toList()
        for ( (tvAnswer, answer) in tvAnswers zip questions[currentQuestionIndex].answers) {
            tvAnswer.text = answer
        }

        tvQuestionCount.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
    }

    fun answerSelected(view: View) {

        // Check what answer was clicked
        for (tv_answer in tvAnswers) {
            if (tv_answer.id == view.id) {
                val selectedAnswerLetter = tv_answer.tag.toString()
                if (!userAnswers.contains(selectedAnswerLetter))
                    userAnswers.add(selectedAnswerLetter)
            }
        }

        checkAnswer()

        if (userAnswers.size == questions[currentQuestionIndex].correctAnswers.size) {
            showCorrectAnswers()
            showExplanation()
        }

    }

    private fun checkAnswer() {

        for (answer in userAnswers) {
            val index = answer[0].toInt() - "A"[0].toInt()
            if (!correctAnswers.contains(answer)) {

                answersBackground[index].setBackgroundResource(R.drawable.wrong_answer)
                tvAnswers[index].setTextColor(Color.WHITE)
            }
            else {
                answersBackground[index].setBackgroundResource(R.drawable.right_answer)
            }
        }

    }

    private fun showCorrectAnswers() {

        for (answer in correctAnswers) {
            val index = answer[0].toInt() - "A"[0].toInt()
            answersBackground[index].setBackgroundResource(R.drawable.right_answer)
            tvAnswers[index].setTextColor(Color.WHITE)
        }
    }

    fun nextButtonCLicked(view: View) {

        if (nextQuestionButton.text == "Finish")
            this.finish()

        if (currentQuestionIndex < questions.size-1) {
            currentQuestionIndex += 1

            for ( (bg, tvAnswer) in answersBackground zip tvAnswers) {
                bg.setBackgroundResource(R.drawable.default_textview)
                tvAnswer.setTextColor(Color.BLACK)
            }
            userAnswers.clear()

            hideExplanation()
            showNextQuestion()
        }

        if (currentQuestionIndex == questions.size-1) {
            nextQuestionButton.text = "Finish"
        }
    }

    private fun showExplanation() {
        explanationLabel.visibility = View.VISIBLE
        explanationText.visibility = View.VISIBLE
        explanationText.text = questions[currentQuestionIndex].explanation
    }

    private fun hideExplanation() {
        explanationLabel.visibility = View.INVISIBLE
        explanationText.visibility = View.INVISIBLE
    }
}
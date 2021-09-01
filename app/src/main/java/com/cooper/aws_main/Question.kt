package com.cooper.aws_main

data class Question(
        val question: String,
        val answers: List<String>,
        val correctAnswers: MutableList<String>,
        val explanation: String
        )
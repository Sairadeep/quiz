package com.turbotechnologies.quiz.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.turbotechnologies.quiz.model.Repository

class QuizViewModel(private val dataRepository: Repository = Repository()) : ViewModel() {

    private val qns = MutableLiveData<List<Map<String, String>>>()
    val question: LiveData<List<Map<String, String>>>
        get() = qns

    private val correctInput = MutableLiveData<Int>()
    val ca: LiveData<Int>
        get() = correctInput

    private val wrongInput = MutableLiveData<Int>()
    val wa: LiveData<Int>
        get() = wrongInput

    fun qnestions() {
        dataRepository.qns { questions ->
            qns.value = questions
        }
    }

    fun scoreData() {
        dataRepository.score { Correct, Wrong ->
            correctInput.value = Correct
            wrongInput.value = Wrong
        }
    }


}
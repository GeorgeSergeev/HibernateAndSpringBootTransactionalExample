package com.example.postgresdemo.service;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Answer;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.AnswerRepository;
import com.example.postgresdemo.repository.QuestionRepository;
import netscape.security.ForbiddenTargetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;


    @Transactional(rollbackFor = ResourceNotFoundException.class)
    public Question createQuestionAndAnswers (Question question) {
        Question newQuestion = questionRepository.save(question);
        for (int i=0; i<10; i++) {
            Answer answ = new Answer();
            answ.setText(String.format("Answer %d to question %d",i,question.getId().intValue()));
            try {
                createAnswer(newQuestion.getId(), answ); //correct
      //          createAnswer(newQuestion.getId()+1, answ); //throw exception
            } catch (ResourceNotFoundException ex) {
                System.out.println("Write log");
                throw ex;
            } catch (ForbiddenTargetException exF) {
                System.out.println("Write log: Forbidden to creating");
            }
        }
        return  newQuestion;
    }

    @Transactional(rollbackFor = ForbiddenTargetException.class)
    public  Answer createAnswer (Long questionId, Answer answer) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    answer.setQuestion(question);
                    if (answer.getText().contains("5")) {
                        System.out.println("Exception for "+answer.getText());
                        throw new ForbiddenTargetException();
                    }
                    return answerRepository.save(answer);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionId));
    }



}

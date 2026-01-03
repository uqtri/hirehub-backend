package org.example.hirehub.service;

import org.example.hirehub.dto.questionbank.*;
import org.example.hirehub.entity.Question;
import org.example.hirehub.entity.QuestionBank;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.ResourceNotFoundException;
import org.example.hirehub.repository.QuestionBankRepository;
import org.example.hirehub.repository.QuestionRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionBankService {
    
    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    
    public QuestionBankService(
            QuestionBankRepository questionBankRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository
    ) {
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public QuestionBankDTO createQuestionBank(CreateQuestionBankDTO dto) {
        User recruiter = userRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        
        QuestionBank questionBank = new QuestionBank();
        questionBank.setRecruiter(recruiter);
        questionBank.setTitle(dto.getTitle());
        questionBank.setDescription(dto.getDescription());
        questionBank.setCategory(dto.getCategory());
        questionBank.setCreatedAt(LocalDateTime.now());
        
        QuestionBank savedBank = questionBankRepository.save(questionBank);
        
        // Create questions
        List<Question> questions = new ArrayList<>();
        if (dto.getQuestions() != null) {
            for (int i = 0; i < dto.getQuestions().size(); i++) {
                Question question = new Question();
                question.setQuestionBank(savedBank);
                question.setContent(dto.getQuestions().get(i));
                question.setOrderIndex(i);
                question.setCreatedAt(LocalDateTime.now());
                questions.add(question);
            }
            questionRepository.saveAll(questions);
        }
        
        savedBank.setQuestions(questions);
        return toDTO(savedBank);
    }
    
    public List<QuestionBankDTO> getQuestionBanksByRecruiterId(Long recruiterId) {
        return questionBankRepository.findByRecruiterIdAndIsDeletedFalse(recruiterId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public QuestionBankDTO getQuestionBankById(Long id) {
        QuestionBank bank = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found"));
        return toDTO(bank);
    }
    
    @Transactional
    public QuestionBankDTO updateQuestionBank(Long id, CreateQuestionBankDTO dto) {
        QuestionBank bank = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found"));
        
        bank.setTitle(dto.getTitle());
        bank.setDescription(dto.getDescription());
        bank.setCategory(dto.getCategory());
        bank.setUpdatedAt(LocalDateTime.now());
        
        // Delete old questions and create new ones
        if (bank.getQuestions() != null) {
            questionRepository.deleteAll(bank.getQuestions());
        }
        
        List<Question> questions = new ArrayList<>();
        if (dto.getQuestions() != null) {
            for (int i = 0; i < dto.getQuestions().size(); i++) {
                Question question = new Question();
                question.setQuestionBank(bank);
                question.setContent(dto.getQuestions().get(i));
                question.setOrderIndex(i);
                question.setCreatedAt(LocalDateTime.now());
                questions.add(question);
            }
            questionRepository.saveAll(questions);
        }
        
        bank.setQuestions(questions);
        QuestionBank savedBank = questionBankRepository.save(bank);
        return toDTO(savedBank);
    }
    
    @Transactional
    public void deleteQuestionBank(Long id) {
        QuestionBank bank = questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question bank not found"));
        bank.setDeleted(true);
        questionBankRepository.save(bank);
    }
    
    private QuestionBankDTO toDTO(QuestionBank bank) {
        QuestionBankDTO dto = new QuestionBankDTO();
        dto.setId(bank.getId());
        dto.setRecruiterId(bank.getRecruiter().getId());
        dto.setRecruiterName(bank.getRecruiter().getName());
        dto.setTitle(bank.getTitle());
        dto.setDescription(bank.getDescription());
        dto.setCategory(bank.getCategory());
        dto.setCreatedAt(bank.getCreatedAt());
        dto.setUpdatedAt(bank.getUpdatedAt());
        
        if (bank.getQuestions() != null) {
            List<QuestionDTO> questionDTOs = bank.getQuestions().stream()
                    .filter(q -> !q.isDeleted())
                    .map(q -> new QuestionDTO(q.getId(), q.getContent(), q.getOrderIndex()))
                    .collect(Collectors.toList());
            dto.setQuestions(questionDTOs);
        }
        
        return dto;
    }
}


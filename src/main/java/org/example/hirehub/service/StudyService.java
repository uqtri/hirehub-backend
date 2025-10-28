package org.example.hirehub.service;

import org.example.hirehub.dto.study.CreateStudyRequestDTO;
import org.example.hirehub.dto.study.UpdateStudyRequestDTO;
import org.example.hirehub.entity.Study;
import org.example.hirehub.entity.University;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.StudyMapper;
import org.example.hirehub.repository.StudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;
    private final UserService userService;
    private final UniversityService universityService;

    public StudyService(StudyRepository studyRepository, StudyMapper studyMapper, UserService userService, UniversityService universityService) {
        this.studyRepository = studyRepository;
        this.studyMapper = studyMapper;
        this.userService = userService;
        this.universityService = universityService;
    }

    public List<Study> findStudiesByUserId(Long userId) {
        return studyRepository.findStudiesByUserId(userId);
    }
    public Study create(CreateStudyRequestDTO request) {

        Study study = new Study();
        studyMapper.updateFromDTO(study, request);
        User user = userService.getUserById(request.getUserId());
        University university = universityService.findById(request.getUniversityId());
        study.setUniversity(university);
        study.setUser(user);
        return studyRepository.save(study);
    }
    public Study updateById(Long id, UpdateStudyRequestDTO request) {

        Study study = studyRepository.findById(id).orElse(null);
        if(study == null) {
            return null;
        }
        studyMapper.updateFromDTO(study, request);
        if(request.getUniversityId() != null) {
            University university = universityService.findById(request.getUniversityId());
            study.setUniversity(university);
        }
        return studyRepository.save(study);
    }
    public void deleteById(Long id) {
        studyRepository.deleteById(id);
    }

}

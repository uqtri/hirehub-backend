package org.example.hirehub.controller;


import org.example.hirehub.dto.study.CreateStudyRequestDTO;
import org.example.hirehub.dto.study.StudyDetailDTO;
import org.example.hirehub.dto.study.StudySummaryDTO;
import org.example.hirehub.dto.study.UpdateStudyRequestDTO;
import org.example.hirehub.entity.Study;
import org.example.hirehub.mapper.StudyMapper;
import org.example.hirehub.service.StudyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/studies")
public class StudyController {


    private final StudyService studyService;
    private final StudyMapper studyMapper;

    public StudyController(StudyService studyService, StudyMapper studyMapper) {
        this.studyService = studyService;
        this.studyMapper = studyMapper;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, ?>> getStudiesByUserId(@PathVariable Long id) {
        List<StudyDetailDTO> studies = studyService.findStudiesByUserId(id).stream().map(studyMapper::toDTO).toList();

        return ResponseEntity.ok().body(Map.of("data", studies));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, ?>> create(@RequestBody CreateStudyRequestDTO request) {
        Study study = studyService.create(request);

        return ResponseEntity.ok().body(Map.of("data", studyMapper.toDTO(study)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudy(@PathVariable Long id) {
        studyService.deleteById(id);
        return ResponseEntity.ok().body(Map.of("message", "Xóa study thành công"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateStudy(@PathVariable Long id, @RequestBody UpdateStudyRequestDTO request) {

        Study study = studyService.updateById(id, request);
        if(study == null) {
            return ResponseEntity.badRequest().body(Map.of("message","Không tìm thấy study"));
        }
        return ResponseEntity.ok().body(Map.of("data", studyMapper.toDTO(study)));
    }

}

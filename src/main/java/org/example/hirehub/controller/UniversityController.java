package org.example.hirehub.controller;

import org.example.hirehub.entity.University;

import org.example.hirehub.service.UniversityService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/universities")
public class UniversityController {
    private final UniversityService universityService;

    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping("")
    public List<University> getAllUniversities() {
        return universityService.findAll();
    }
}

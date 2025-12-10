package org.example.hirehub.controller;

import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.entity.Language;
import org.example.hirehub.mapper.LanguageMapper;
import org.example.hirehub.service.LanguageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;
    private final LanguageMapper languageMapper;
    public LanguageController(LanguageService languageService, LanguageMapper languageMapper) {
        this.languageService = languageService;
        this.languageMapper = languageMapper;
    }
    @GetMapping("")
    public List<LanguageDetailDTO> getLanguages() {
        return languageService.getLanguages().stream().map(languageMapper::toDTO).toList();
    }

}

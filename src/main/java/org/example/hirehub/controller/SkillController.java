package org.example.hirehub.controller;

import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.mapper.SkillMapper;
import org.example.hirehub.service.SkillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper skillMapper;
    public SkillController(SkillService skillService, SkillMapper skillMapper) {
        this.skillService = skillService;
        this.skillMapper = skillMapper;
    }
    @GetMapping("")
    public List<SkillSummaryDTO> getSkills() {
        return skillService.getSkills().stream().map(skillMapper::toDTO).toList();
    }

}

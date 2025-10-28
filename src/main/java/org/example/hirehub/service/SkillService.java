package org.example.hirehub.service;

import org.springframework.stereotype.Service;

import java.util.List;

import org.example.hirehub.repository.SkillRepository;
import org.example.hirehub.entity.Skill;


@Service
public class SkillService {

    private final SkillRepository skillRepository;

    SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }
    public List<Skill> findSkillsByIds (List<Long> ids) {
        return skillRepository.findSkillsByIds(ids);
    }

    public List<Skill> getSkills() {
        return skillRepository.findAll();
    }


}

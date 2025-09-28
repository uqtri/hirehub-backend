package org.example.hirehub.service;

import org.example.hirehub.entity.Skill;
import org.example.hirehub.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }
    List<Skill> getSkillsByIds (List<Long> ids) {
        return skillRepository.findSkillsByIds(ids);
    }

}

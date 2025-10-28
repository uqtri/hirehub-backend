package org.example.hirehub.service;

import org.example.hirehub.entity.LanguageLevel;
import org.example.hirehub.repository.LanguageLevelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageLevelService {

    private final LanguageLevelRepository languageLevelRepository;
    public LanguageLevelService(LanguageLevelRepository languageLevelRepository) {
        this.languageLevelRepository = languageLevelRepository;
    }

    public LanguageLevel save(LanguageLevel data){
        return languageLevelRepository.save(data);
    }
    public List<LanguageLevel> findByIds(List<Long> ids){
        return languageLevelRepository.findByIds(ids);
    }
}

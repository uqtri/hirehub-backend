package org.example.hirehub.service;

import org.example.hirehub.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.hirehub.repository.LanguageRepository;
import org.example.hirehub.entity.Language;


@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getLanguages() {
        return languageRepository.findAll();
    }


}

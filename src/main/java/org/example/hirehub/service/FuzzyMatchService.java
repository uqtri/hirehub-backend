package org.example.hirehub.service;


import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
public class FuzzyMatchService {
    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    private String removeDiacritics(String text) {
        if (text == null) {
            return null;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{Mn}", "");
    }
    public double getJaroWinklerSimilarity(String target, String candidate) {
        if (target == null || candidate == null) {
            return 0.0;
        }

        String cleanTarget = removeDiacritics(target).toLowerCase();
        String cleanCandidate = removeDiacritics(candidate).toLowerCase();

        return jaroWinkler.apply(cleanTarget, cleanCandidate);
    }
    public boolean isSimilar(String target, String candidate, double threshold) {
        double score = getJaroWinklerSimilarity(target, candidate);
        return score >= threshold;
    }
}

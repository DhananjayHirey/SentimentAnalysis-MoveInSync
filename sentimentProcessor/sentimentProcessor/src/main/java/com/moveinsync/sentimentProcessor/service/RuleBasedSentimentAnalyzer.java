package com.moveinsync.sentimentProcessor.service;

import com.moveinsync.sentimentProcessor.analyzer.SentimentAnalyzer;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedSentimentAnalyzer implements SentimentAnalyzer {

    @Override
    public double analyze(String text, int rating) {
        if (text == null)
            return rating;

        String lower = text.toLowerCase();
        double score = rating;

        // Negative indicators
        if (lower.contains("rude") || lower.contains("late") || lower.contains("bad") ||
                lower.contains("dirty") || lower.contains("unsafe") || lower.contains("unprofessional")) {
            score -= 1.5;
        }

        // Positive indicators
        if (lower.contains("excellent") || lower.contains("good") || lower.contains("best") ||
                lower.contains("clean") || lower.contains("polite") || lower.contains("safe")) {
            score += 1.0;
        }

        return Math.max(1.0, Math.min(score, 5.0));
    }
}

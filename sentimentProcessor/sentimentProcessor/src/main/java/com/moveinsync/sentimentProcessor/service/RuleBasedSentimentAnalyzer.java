package com.moveinsync.sentimentProcessor.service;

import com.moveinsync.sentimentProcessor.analyzer.SentimentAnalyzer;
import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedSentimentAnalyzer implements SentimentAnalyzer {

    @Override
    public SentimentLabel analyze(String text) {
        if (text == null || text.trim().isEmpty()) {
            return SentimentLabel.NEUTRAL;
        }

        String lower = text.toLowerCase();

        // Negative indicators
        if (lower.contains("rude") || lower.contains("late") || lower.contains("bad") ||
                lower.contains("dirty") || lower.contains("unsafe") || lower.contains("unprofessional") ||
                lower.contains("poor") || lower.contains("slow")) {
            return SentimentLabel.NEGATIVE;
        }

        // Positive indicators
        if (lower.contains("excellent") || lower.contains("good") || lower.contains("best") ||
                lower.contains("clean") || lower.contains("polite") || lower.contains("safe") ||
                lower.contains("punctual") || lower.contains("awesome")) {
            return SentimentLabel.POSITIVE;
        }

        return SentimentLabel.NEUTRAL;
    }
}

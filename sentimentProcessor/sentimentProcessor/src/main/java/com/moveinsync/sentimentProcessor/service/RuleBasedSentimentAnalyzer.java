package com.moveinsync.sentimentProcessor.service;


import com.moveinsync.sentimentProcessor.analyzer.SentimentAnalyzer;
import org.springframework.stereotype.Service;


@Service
public class RuleBasedSentimentAnalyzer implements SentimentAnalyzer {

    @Override
    public double analyze(String text, int rating) {
        double score = rating;
        if(text.contains("rude")||text.contains("late")){
            score-=1;
        }
        if (text.contains("excellent") || text.contains("good")) {
            score += 1;
        }
        return Math.max(1, Math.min(score, 5));
    }
}

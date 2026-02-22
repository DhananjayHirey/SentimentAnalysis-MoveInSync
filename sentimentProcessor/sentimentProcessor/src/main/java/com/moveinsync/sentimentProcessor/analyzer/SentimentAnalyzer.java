package com.moveinsync.sentimentProcessor.analyzer;

public interface SentimentAnalyzer {
    double analyze(String text, int rating);
}

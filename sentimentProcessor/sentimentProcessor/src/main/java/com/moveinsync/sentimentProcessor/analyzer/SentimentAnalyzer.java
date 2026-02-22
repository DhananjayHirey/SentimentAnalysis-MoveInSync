package com.moveinsync.sentimentProcessor.analyzer;

public interface SentimentAnalyzer {
    SentimentLabel analyze(String text);
}

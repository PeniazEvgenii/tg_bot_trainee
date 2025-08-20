package com.example.secondTelegramBot.sender;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramMessageSplitter {
    private static final int MAX_LENGTH = 4096;

    public List<String> splitMessage(String text) {
        List<String> parts = new ArrayList<>();

        while (text.length() > MAX_LENGTH) {
            int splitIndex = findSplitIndex(text, MAX_LENGTH);
            parts.add(text.substring(0, splitIndex));
            text = text.substring(splitIndex).trim();
        }

        if(!text.isEmpty()) {
            parts.add(text);
        }
        return parts;
    }

    private int findSplitIndex(String text, int maxLength) {
        int idx = text.lastIndexOf("\n\n", maxLength);
        if(idx != -1 && idx > maxLength / 2) {
            return idx;
        }

        // Потом ищем конец предложения
        idx = text.lastIndexOf(". ", maxLength);
        if (idx != -1 && idx > maxLength / 2) {
            return idx + 1; // включаем точку
        }

        idx = text.lastIndexOf("! ", maxLength);
        if (idx != -1 && idx > maxLength / 2) {
            return idx + 1;
        }

        idx = text.lastIndexOf("? ", maxLength);
        if (idx != -1 && idx > maxLength / 2) {
            return idx + 1;
        }

        return maxLength;
    }
}

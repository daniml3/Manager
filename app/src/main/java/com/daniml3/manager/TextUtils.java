package com.daniml3.manager;

public class TextUtils {

    public static String getLastLines(String paragraph, int count) {
        String[] splittedParagraph = paragraph.split("\n");
        int lineCount = splittedParagraph.length;
        StringBuilder stringBuilder = new StringBuilder();

        if (count > lineCount) {
            count = lineCount;
        }

        for (int i = lineCount - count; i < lineCount; i++) {
            try {
                stringBuilder.append(splittedParagraph[i]);
                stringBuilder.append("\n");
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return stringBuilder.toString();
    }
}

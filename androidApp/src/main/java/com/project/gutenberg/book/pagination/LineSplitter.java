package com.project.gutenberg.book.pagination;

import com.project.gutenberg.book.view.BookFormatting;

import java.util.ArrayList;
import java.util.List;

public class LineSplitter {
    public static Object[] split(LineMeasurer lineMeasurer, BookFormatting formatting, String[] words, Integer wordIndex, Integer paragraphIndex, Integer lineCount,Integer[] textBoundaries, String[] linesOfText, float[] wordWidths) {
        Object[] returnObject =  buildForward_(lineMeasurer, formatting, words, wordIndex, paragraphIndex, lineCount, textBoundaries, linesOfText, wordWidths);
        return returnObject;
    }
    public static Object[] buildForward_(LineMeasurer lineMeasurer, BookFormatting formatting, String[] words, int wordIndex, int paragraphIndex, int lineCount, Integer[] textBoundaries, String[] linesOfText, float[] wordWidths) {
        Object[] returnObject = new Object[5];
        returnObject[1]=new Boolean(false);
        int wordsAdded = 0;
        int lineWordsAdded = 0;
        float spaceWidth = lineMeasurer.measureWidth(" ");
        float currentWidth = lineMeasurer.measureWidth(linesOfText[lineCount]);
        for (int i=wordIndex; i < words.length; i++) {
            float newWidth = currentWidth + spaceWidth + wordWidths[i];
            if (newWidth > formatting.getLineWidth()) {
                if (lineWordsAdded == 0) {
                    int overflowIndex = splitSingleWord(words[i], wordWidths[i], lineMeasurer, formatting);
                    String firstPart = words[i].substring(0,overflowIndex);
                    linesOfText[lineCount] = firstPart;
                    words[i] = words[i].substring(overflowIndex,words[i].length());
                    wordWidths[i] = wordWidths[i]- lineMeasurer.measureWidth(firstPart);
                }
                i--;
                lineCount++;
                lineWordsAdded=0;
                if (lineCount == linesOfText.length) { // paragraph cutoff
                    textBoundaries[3] = textBoundaries[0];
                    textBoundaries[4] = paragraphIndex;
                    textBoundaries[5] = i;
                    returnObject[1]=true;
                    break;
                } else {
                    linesOfText[lineCount] = "";
                    currentWidth=0;
                }
            } else {
                wordsAdded++;
                lineWordsAdded++;
                linesOfText[lineCount] += " " + words[i];
                currentWidth = newWidth;
            }
        }
        returnObject[0] = wordIndex;
        returnObject[2]=paragraphIndex;
        returnObject[3]=lineCount;
        returnObject[4]= wordsAdded;
        return returnObject;
    }
    public static float[] wordWidths(String[] words, LineMeasurer lineMeasurer) {
        float[] widths = new float[words.length];
        for (int i=0; i < words.length; i++) {
            widths[i] = lineMeasurer.measureWidth(words[i]);
        }
        return widths;
    }
    private static int splitSingleWord(String word, float wordWidth, LineMeasurer measurer, BookFormatting formatting) {
        for (int i=0; i < word.length(); i++) {
            if (measurer.measureWidth(word.substring(0, i)) > formatting.getLineWidth()) {
                return i;
            }
        }
        return word.length()-1;
    }
    public static String[] fastSplit(final String text, char separator) {
        final List<String> result = new ArrayList<String>();
        if (text != null && text.length() > 0) {
            int index1 = 0;
            int index2 = text.indexOf(separator);
            while (index2 >= 0) {
                String token = text.substring(index1, index2);
                result.add(token);
                index1 = index2 + 1;
                index2 = text.indexOf(separator, index1);
            }
            if (index1 < text.length() - 1) {
                result.add(text.substring(index1));
            }
        }
        return result.toArray(new String[result.size()]);
    }
}

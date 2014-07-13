package com.project.gutenberg.util;

public class SeekbarConverter {
    public static int convertFontScaleToSeekbarPosition(float scale) {
        if (scale == 0.5f) return 0;
        if (scale == 0.75f) return 1;
        if (scale == 1.0f) return 2;
        if (scale == 1.25f) return 3;
        if (scale == 1.5f) return 4;
        if (scale == 2.0f) return 5;
        if (scale == 3.0f) return 6;
        return 2;
    }
    public static float convertSeekbarPositionToFontScale(int position) {
        if (position == 0) return 0.5f;
        if (position == 1) return 0.75f;
        if (position == 2) return 1.0f;
        if (position == 3) return 1.25f;
        if (position == 4) return 1.5f;
        if (position == 5) return 2.0f;
        if (position == 6) return 3.0f;
        return 1.0f;
    }
    public static String convertSeekbarPositionToLabel(int position) {
        if (position == 0) return "0.5x";
        if (position == 1) return "0.75x";
        if (position == 2) return "1.0x";
        if (position == 3) return "1.25x";
        if (position == 4) return "1.5x";
        if (position == 5) return "2.0x";
        if (position == 6) return "3.0x";
        return "1.0x";
    }
}

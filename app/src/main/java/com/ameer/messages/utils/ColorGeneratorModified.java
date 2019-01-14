package com.ameer.messages.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorGeneratorModified {

    public static ColorGeneratorModified DEFAULT;

    public static ColorGeneratorModified MATERIAL;

    static {
        DEFAULT = create(Arrays.asList(
                0xfff16364,
                0xfff58559,
                0xfff9a43e,
                0xffe4c62e,
                0xff67bf74,
                0xff59a2be,
                0xff2093cd,
                0xffad62a7,
                0xff805781
        ));
        MATERIAL = create(Arrays.asList(
                0xffF44336,
                0xff9C27B0,
                0xffE91E63,
                0xff673AB7,
                0xff3F51B5,
                0xff2196F3,
                0xff03A9F4,
                0xff00BCD4,
                0xff009688,
                0xff4CAF50,
                0xff8BC34A,
                0xffFF9800,
                0xffFF5722,
                0xff795548,
                0xff9E9E9E,
                0xff607D8B
        ));
    }

    private final List<Integer> mColors;
    private final Random mRandom;

    public static ColorGeneratorModified create(List<Integer> colorList) {
        return new ColorGeneratorModified(colorList);
    }

    private ColorGeneratorModified(List<Integer> colorList) {
        mColors = colorList;
        mRandom = new Random(System.currentTimeMillis());
    }

    public int getRandomColor() {
        return mColors.get(mRandom.nextInt(mColors.size()));
    }

    public int getColor(Object key) {
        return mColors.get(Math.abs(key.hashCode()) % mColors.size());
    }
}

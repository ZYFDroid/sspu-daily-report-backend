package com.zyfdroid.dailyreportreminder.tests;

import com.zyfdroid.dailyreportreminder.utils.TimeUtils;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class TestCase {
    private static final String TAG = "ExampleUnitTest";

    @org.junit.Test
    public void addition_isCorrect() throws Exception {
        System.out.println("Begin DateTime test");
        System.out.println(TimeUtils.getDayStamp(new Date()));
        System.out.println(TimeUtils.sdf.format(TimeUtils.parseDayStamp(7280)));
        System.out.println(TimeUtils.sdf.format(TimeUtils.computeNextTime(7280,TimeUtils.parse("2020-10-26 06:59:50"))));
    }
}
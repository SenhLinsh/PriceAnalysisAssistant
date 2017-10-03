package com.linsh.paa;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String s = "http://fdas.com?id=372402375&itd=dfsdf";
        s = s.replaceAll(".+\\?id=(\\d+).+", "$1");
        assertEquals(s, "");
    }
}
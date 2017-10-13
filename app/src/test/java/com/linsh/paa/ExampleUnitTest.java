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
        String s = "#3打打工发噶十多个";
        String lowStr = s.replaceFirst("(#[12][^#]+)?.*", "$1");
        String descendStr = s.replaceFirst("(#[12][^#]+)?(#[34][^#]+)?", "$2");
        assertEquals(lowStr, descendStr);
    }
}
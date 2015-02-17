package org.tengel.splay;

import java.text.DecimalFormat;

public class Util
{
    public static String msToStr(int ms)
    {
        int h = (ms / 1000) / (60 * 60);
        int min = (ms / 1000) / 60 % 60;
        int sec = (ms / 1000) % 60;
        DecimalFormat format = new DecimalFormat("00");
        return format.format(h) + ":" + format.format(min) + ":" +
               format.format(sec);
    }

}
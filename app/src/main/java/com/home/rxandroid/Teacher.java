package com.home.rxandroid;

import java.io.Serializable;

/**
 * Created by wade on 2017/10/8.
 */

public class Teacher implements Serializable{
    private String string1;
    private String string2;
    private int i = -1;

    public Teacher(String string1, int i, String string2) {
        this.string1 = string1;
        this.i = i;
        this.string2 = string2;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}

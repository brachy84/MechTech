package com.brachy84.mechtech.integration.crafttweaker;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.mechtech.Math")
@ZenRegister
public class CTMath {

    private CTMath() {}

    @ZenDoc("A value equal to 2.7182818284590452354")
    @ZenProperty
    public static final double e = Math.E;

    @ZenDoc("A value equal to 3.14159265358979323846")
    @ZenProperty
    public static final double pi = Math.PI;

    @ZenMethod
    public static double exp(double v) {
        return Math.exp(v);
    }

    @ZenMethod
    public static double pow(double v, double exp) {
        return Math.pow(v, exp);
    }

    @ZenMethod
    public static double root(double v, double exp) {
        return Math.pow(v, 1 / exp);
    }

    @ZenMethod
    public static double sqrt(double v) {
        return Math.sqrt(v);
    }

    @ZenMethod
    public static double sin(double v) {
        return Math.sin(v);
    }

    @ZenMethod
    public static double cos(double v) {
        return Math.cos(v);
    }

    @ZenMethod
    public static double tan(double v) {
        return Math.tan(v);
    }

    @ZenMethod
    public static double asin(double v) {
        return Math.asin(v);
    }

    @ZenMethod
    public static double acos(double v) {
        return Math.acos(v);
    }

    @ZenMethod
    public static double atan(double v) {
        return Math.atan(v);
    }

    @ZenMethod
    public static double toRadians(double v) {
        return Math.toRadians(v);
    }

    @ZenMethod
    public static double toDegree(double v) {
        return Math.toDegrees(v);
    }

    @ZenMethod
    public static double log(double v) {
        return Math.log(v);
    }

    @ZenMethod
    public static double log10(double v) {
        return Math.log10(v);
    }

    @ZenMethod
    public static int ceil(double v) {
        return (int) Math.ceil(v);
    }

    @ZenMethod
    public static int floor(double v) {
        return (int) Math.floor(v);
    }

    @ZenMethod
    public static int round(double v) {
        return (int) Math.round(v);
    }

    @ZenMethod
    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    @ZenMethod
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    @ZenMethod
    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    @ZenMethod
    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    @ZenMethod
    public static int clamp(int v, int min, int max) {
        return Math.min(max, Math.max(v, min));
    }

    @ZenMethod
    public static double clamp(double v, double min, double max) {
        return Math.min(max, Math.max(v, min));
    }
}

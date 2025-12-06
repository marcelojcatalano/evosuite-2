/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package com.examples.with.different.packagename.solver;

public class TestCaseRegex {

    public static boolean testConcat(String str) {
        return str != null && str.matches("a*b");
    }

    public static boolean testUnion(String str) {
        return str != null && str.matches("a|b");
    }

    public static boolean testOptional(String str) {
        return str != null && str.matches("(a)?");
    }

    public static boolean testString(String str) {
        return str != null && str.matches("hello");
    }

    public static boolean testAnyChar(String str) {
        return str != null && str.matches(".");
    }

    public static boolean testEmpty(String str) {
        return str != null && str.matches("");
    }

    public static boolean testCross(String str) {
        return str != null && str.matches("a+");
    }

    public static boolean testRepeatMin(String str) {
        return str != null && str.matches("a{3,}");
    }

    public static boolean testRepeatMinMax(String str) {
        return str != null && str.matches("a{3,5}");
    }

    public static boolean testRepeatN(String str) {
        return str != null && str.matches("a{5}");
    }

    public static boolean testIntersection(String str) {
        return str != null && str.matches("[0-9&&[345]]");
    }

    public static boolean testChoice(String str) {
        return str != null && str.matches("[abc]");
    }

    public static boolean testRange(String str) {
        return str != null && str.matches("[a-z]");
    }
}

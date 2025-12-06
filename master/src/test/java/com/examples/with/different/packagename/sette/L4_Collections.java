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
package com.examples.with.different.packagename.sette;

/*
 * SETTE - Symbolic Execution based Test Tool Evaluator
 *
 * SETTE is a tool to help the evaluation and comparison of symbolic execution
 * based test input generator tools.
 *
 * Budapest University of Technology and Economics (BME)
 *
 * Authors: Lajos Cseppentő <lajos.cseppento@inf.mit.bme.hu>, Zoltán Micskei
 * <micskeiz@mit.bme.hu>
 *
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class L4_Collections {
    private L4_Collections() {
        throw new UnsupportedOperationException("Static class");
    }

    public static boolean guessSize(int s) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);

        return list.size() == s;
    }

    public static boolean guessElements(int a, int b) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);

        return list.get(0) == a && list.get(1) == b;
    }

    public static boolean guessSizeAndElements(int s, int a, int b) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);

        return list.size() == s && list.get(0) == a && list.get(1) == b;
    }

    public static boolean guessIndices(int a, int b) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);

        return list.get(a) == 20 && list.get(b) == 30;
    }

    public static boolean guessElementAndIndex(int a, int i) {
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);

        return list.get(i) == a;
    }

    public static boolean guessVectorWithSize(
            Vector v) {
        return v.size() == 3;
    }

    public static boolean guessGenericVectorWithSize(Vector<Integer> v) {
        return v.size() == 3;
    }

    public static boolean guessGenericVectorWithElement(Vector<Integer> v) {
        return v.size() == 3 && v.get(0).equals(5);
    }

    public static boolean guessListWithSize(List l) {
        return l.size() == 3;
    }

    public static boolean guessGenericListWithSize(List<Integer> l) {
        return l.size() == 3;
    }

    public static boolean guessGenericListWithElement(List<Integer> l) {
        return l.size() == 3 && l.get(0).equals(5);
    }
}
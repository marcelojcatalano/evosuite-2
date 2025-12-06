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
package org.evosuite.symbolic.solver.smt;

import org.evosuite.symbolic.solver.SolverResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestModelParser {

    @Test
    public void parseIntegerValues() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var0 () Int 0)\n" +
                "(define-fun var1 () Int 10)\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals(0L, solution.getValue("var0"));
        assertEquals(10L, solution.getValue("var1"));
    }

    @Test
    public void parseBlankStringSolution() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var8 () String \" \")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals(" ", solution.getValue("var8"));
    }

    @Test
    public void parseEmptyStringSolution() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var8 () String \"\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals("", solution.getValue("var8"));
    }

    @Test
    public void parseSingleStringSolution() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var8 () String \"Hello\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals("Hello", solution.getValue("var8"));
    }

    @Test
    public void parseSingleLineStringSolution()
            throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var8 () String \"Hello World\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals("Hello World", solution.getValue("var8"));
    }

    @Test
    public void parseMultiLineSolution() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "(define-fun var8 () String \"Hello\nBeautiful\nWorld\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals("Hello\nBeautiful\nWorld", solution.getValue("var8"));
    }

    @Test
    public void parseRealZeroValues() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "  (define-fun var0 () Real 0.0 )\n" +
                "  (define-fun var1 () Real 0.0 )\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals(0.0, solution.getValue("var0"));
        assertEquals(0.0, solution.getValue("var1"));
    }

    @Test
    public void parseRationalValues() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "  (define-fun var0 () Real 0.0)\n" +
                "  (define-fun var1 () Real (/ 3141592653589793 1000000000000000))\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals(0.0, solution.getValue("var0"));
        assertEquals(Math.PI, solution.getValue("var1"));
    }

    @Test
    public void parseEncodedString() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "  (define-fun var0 () String\n" +
                "      \"\\x00\\x00\\x00\\x00\\x00\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals(String.valueOf(new char[]{0, 0, 0, 0, 0}), solution.getValue("var0"));
    }

    @Test
    public void parseEscapedChars() throws Exception {
        String result_str = "sat\n" +
                "(model\n" +
                "  (define-fun var0 () String\n" +
                "      \"\\\\ \")\n" +
                "  (define-fun var1 () String\n" +
                "      \"\\n \")\n" +
                "  (define-fun var2 () String\n" +
                "      \"\\t \")\n" +
                "  (define-fun var3 () String\n" +
                "      \"\\b \")\n" +
                "  (define-fun var4 () String\n" +
                "      \"\\\\x00\")\n" +
                "  (define-fun var5 () String\n" +
                "      \"Hello\\x00World\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        assertEquals("\\ ", solution.getValue("var0"));
        assertEquals("\n ", solution.getValue("var1"));
        assertEquals("\t ", solution.getValue("var2"));
        assertEquals("\b ", solution.getValue("var3"));
        assertEquals("\\x00", solution.getValue("var4"));
        assertEquals(String.valueOf(new char[]{'H', 'e', 'l', 'l', 'o', 0, 'W', 'o', 'r', 'l', 'd'}),
                solution.getValue("var5"));
    }

    @Test
    public void parseChar01() throws Exception {

        String result_str = "sat\n" +
                "(model\n" +
                " (define-fun var0 () String\n" +
                "  \"\\x01\")\n" +
                ")\n";
        SmtModelParser parser = new SmtModelParser();
        SolverResult solution = parser.parse(result_str);
        assertTrue(solution.isSAT());
        char expectedChar = 1;
        char actualChar = ((String) solution.getValue("var0")).charAt(0);
        assertEquals(expectedChar, actualChar);
    }

}

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
package org.evosuite.symbolic.solver;

import com.examples.with.different.packagename.solver.*;
import org.evosuite.symbolic.TestCaseBuilder;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.variable.VariableReference;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

public abstract class TestSolverSimpleMath extends TestSolver {

    private static DefaultTestCase buildTestCaseAdd() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(10);
        VariableReference int1 = tc.appendIntPrimitive(0);

        Method method = TestCaseBinaryOp.class.getMethod("testAdd", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseEq() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(11);
        VariableReference int1 = tc.appendIntPrimitive(11);

        Method method = TestCaseEq.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseNeq() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(1000);
        VariableReference int1 = tc.appendIntPrimitive(11);

        Method method = TestCaseNeq.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseLt() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(2);
        VariableReference int1 = tc.appendIntPrimitive(22);

        Method method = TestCaseLt.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseLte() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(2);
        VariableReference int1 = tc.appendIntPrimitive(2);

        Method method = TestCaseLte.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseGt() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(20);
        VariableReference int1 = tc.appendIntPrimitive(2);

        Method method = TestCaseGt.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseGte() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(20);
        VariableReference int1 = tc.appendIntPrimitive(2);

        Method method = TestCaseGte.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseSub() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(1);
        VariableReference int1 = tc.appendIntPrimitive(11);

        Method method = TestCaseBinaryOp.class.getMethod("testSub", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseMul() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(3);
        VariableReference int1 = tc.appendIntPrimitive(6);

        Method method = TestCaseBinaryOp.class.getMethod("testMul", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseMul2() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(5);
        VariableReference int1 = tc.appendIntPrimitive(2);

        Method method = TestCaseBinaryOp.class.getMethod("testMul2", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    @Test
    public void testAdd() throws Exception {

        DefaultTestCase tc = buildTestCaseAdd();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var1 = (Long) solution.get("var1");

        if (var1.intValue() != 0) {
            Long var0 = (Long) solution.get("var0");
            assertEquals(var0.intValue(), var0.intValue() + var1.intValue());
        }

    }

    @Test
    public void testSub() throws Exception {

        DefaultTestCase tc = buildTestCaseSub();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertEquals(var0.intValue(), var1.intValue() - 10);
    }

    @Test
    public void testMod() throws Exception {

        DefaultTestCase tc = buildTestCaseMod();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertEquals(var0.intValue(), var1.intValue() % 2);
    }

    @Test
    public void testDiv() throws Exception {

        DefaultTestCase tc = buildTestCaseDiv();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);

    }

    @Test
    public void testMul() throws Exception {

        DefaultTestCase tc = buildTestCaseMul();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() != 0);
        assertEquals(var1.intValue(), var0.intValue() * 2);
    }

    @Test
    public void testMul2() throws Exception {

        DefaultTestCase tc = buildTestCaseMul2();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);

    }

    private static DefaultTestCase buildTestCaseDiv() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(4);
        VariableReference int1 = tc.appendIntPrimitive(20);

        Method method = TestCaseBinaryOp.class.getMethod("testDiv", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    private static DefaultTestCase buildTestCaseMod() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(0);
        VariableReference int1 = tc.appendIntPrimitive(6);

        Method method = TestCaseMod.class.getMethod("test", int.class, int.class);
        tc.appendMethod(null, method, int0, int1);
        return tc.getDefaultTestCase();
    }

    @Test
    public void testEq() throws Exception {

        DefaultTestCase tc = buildTestCaseEq();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertEquals(var0.intValue(), var1.intValue());
    }

    @Test
    public void testNeq() throws Exception {

        DefaultTestCase tc = buildTestCaseNeq();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() != var1.intValue());
    }

    @Test
    public void testLt() throws Exception {

        DefaultTestCase tc = buildTestCaseLt();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() < var1.intValue());
    }

    @Test
    public void testLte() throws Exception {

        DefaultTestCase tc = buildTestCaseLte();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() <= var1.intValue());
    }

    @Test
    public void testGt() throws Exception {

        DefaultTestCase tc = buildTestCaseGt();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() > var1.intValue());
    }

    @Test
    public void testGte() throws Exception {

        DefaultTestCase tc = buildTestCaseGte();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");
        Long var1 = (Long) solution.get("var1");

        assertTrue(var0.intValue() >= var1.intValue());
    }

    private static DefaultTestCase buildTestCaseCastRealToInt() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference double0 = tc.appendDoublePrimitive(0.1);

        Method method = TestCaseCastRealToInt.class.getMethod("test", double.class);
        tc.appendMethod(null, method, double0);
        return tc.getDefaultTestCase();
    }

    @Test
    public void testCastRealToInt() throws Exception {

        DefaultTestCase tc = buildTestCaseCastRealToInt();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Double var0 = (Double) solution.get("var0");

        assertTrue(var0 != 0);
        assertEquals(0, var0.intValue());
    }

    @Test
    public void testCastIntToReal() throws Exception {

        DefaultTestCase tc = buildTestCaseCastIntToReal();
        Collection<Constraint<?>> constraints = DefaultTestCaseConcolicExecutor.execute(tc);
        Map<String, Object> solution = solve(getSolver(), constraints);
        assertNotNull(solution);
        Long var0 = (Long) solution.get("var0");

        assertEquals(var0.intValue(), (int) var0.doubleValue());
    }

    private static DefaultTestCase buildTestCaseCastIntToReal() throws SecurityException, NoSuchMethodException {
        TestCaseBuilder tc = new TestCaseBuilder();
        VariableReference int0 = tc.appendIntPrimitive(1);

        Method method = TestCaseCastIntToReal.class.getMethod("test", int.class);
        tc.appendMethod(null, method, int0);
        return tc.getDefaultTestCase();
    }
}

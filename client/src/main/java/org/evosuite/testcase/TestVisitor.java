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

package org.evosuite.testcase;

import org.evosuite.testcase.statements.*;


/**
 * <p>
 * TestVisitor interface.
 * </p>
 *
 * @author fraser
 */
public abstract class TestVisitor {

    /**
     * <p>
     * visitTestCase
     * </p>
     *
     * @param test a {@link org.evosuite.testcase.TestCase} object.
     */
    public abstract void visitTestCase(TestCase test);

    /**
     * <p>
     * visitPrimitiveStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.PrimitiveStatement} object.
     */
    public abstract void visitPrimitiveStatement(PrimitiveStatement<?> statement);

    /**
     * <p>
     * visitFieldStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.FieldStatement} object.
     */
    public abstract void visitFieldStatement(FieldStatement statement);

    /**
     * <p>
     * visitMethodStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.MethodStatement} object.
     */
    public abstract void visitMethodStatement(MethodStatement statement);

    /**
     * <p>
     * visitConstructorStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.ConstructorStatement} object.
     */
    public abstract void visitConstructorStatement(ConstructorStatement statement);

    /**
     * <p>
     * visitArrayStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.ArrayStatement} object.
     */
    public abstract void visitArrayStatement(ArrayStatement statement);

    /**
     * <p>
     * visitAssignmentStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.AssignmentStatement} object.
     */
    public abstract void visitAssignmentStatement(AssignmentStatement statement);

    /**
     * <p>
     * visitNullStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.NullStatement} object.
     */
    public abstract void visitNullStatement(NullStatement statement);

    /**
     * <p>
     * visitPrimitiveExpression
     * </p>
     *
     * @param primitiveExpression a {@link org.evosuite.testcase.statements.PrimitiveExpression} object.
     */
    public abstract void visitPrimitiveExpression(PrimitiveExpression primitiveExpression);


    public abstract void visitFunctionalMockStatement(FunctionalMockStatement functionalMockStatement);

    /**
     * <p>
     * visitStatement
     * </p>
     *
     * @param statement a {@link org.evosuite.testcase.statements.Statement} object.
     */
    public void visitStatement(Statement statement) {

        if (statement instanceof PrimitiveStatement<?> primitiveStatement)
            visitPrimitiveStatement(primitiveStatement);
        else if (statement instanceof FieldStatement fieldStatement)
            visitFieldStatement(fieldStatement);
        else if (statement instanceof ConstructorStatement constructorStatement)
            visitConstructorStatement(constructorStatement);
        else if (statement instanceof MethodStatement methodStatement)
            visitMethodStatement(methodStatement);
        else if (statement instanceof AssignmentStatement assignmentStatement)
            visitAssignmentStatement(assignmentStatement);
        else if (statement instanceof ArrayStatement arrayStatement)
            visitArrayStatement(arrayStatement);
        else if (statement instanceof NullStatement nullStatement)
            visitNullStatement(nullStatement);
        else if (statement instanceof PrimitiveExpression expression)
            visitPrimitiveExpression(expression);
        else if (statement instanceof FunctionalMockStatement mockStatement)
            visitFunctionalMockStatement(mockStatement);
        else
            throw new RuntimeException("Unknown statement type: " + statement);
    }
}

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
package org.evosuite.symbolic;

import org.evosuite.symbolic.expr.*;
import org.evosuite.symbolic.solver.SolverResult;
import org.evosuite.symbolic.solver.SolverUtils;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.numeric.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * ConcolicMutation class.
 * </p>
 *
 * @author Gordon Fraser
 */
public class ConcolicMutation {

    protected static final Logger logger = LoggerFactory.getLogger(ConcolicMutation.class);

    /**
     * Generate new constraint and ask solver for solution
     *
     * @param pathCondition
     * @param targetCondition a {@link org.evosuite.symbolic.BranchCondition} object.
     * @param test            a {@link org.evosuite.testcase.TestCase} object.
     * @return a {@link org.evosuite.testcase.TestCase} object.
     */
    // @SuppressWarnings({ "rawtypes", "unchecked" })
    public static TestCase negateCondition(List<BranchCondition> pathCondition, BranchCondition targetCondition,
                                           TestCase test) {
        List<Constraint<?>> constraints = new LinkedList<>();

        for (BranchCondition b : pathCondition) {
            constraints.addAll(b.getSupportingConstraints());
            if (b == targetCondition) {
                break;
            } else {
                constraints.add(b.getConstraint());
            }
        }
        final Constraint<?> targetConstraint = targetCondition.getConstraint().negate();
        constraints.add(targetConstraint);

        if (!targetConstraint.isSolveable()) {
            logger.info("Found unsolvable constraint: " + targetConstraint);
            // TODO: This is usually the case when the same variable is used for
            // several parameters of a method
            // Could we treat this as a special case?
            return null;
        }

        int size = constraints.size();
        if (size > 0) {
            constraints = reduce(constraints);
            // logger.info("Reduced constraints from " + size + " to " +
            // constraints.size());
            // logger.info("Now solving: " + constraints);
        }

        SolverResult solverResult = SolverUtils.solveQuery(constraints);

        if (solverResult != null) {
            // logger.info(values.toString());
            TestCase newTest = test.clone();

            Map<String, Object> model = solverResult.getModel();
            for (Object key : model.keySet()) {
                Object val = model.get(key);
                if (val != null) {
                    if (val instanceof Long value) {
                        String name = ((String) key).replace("__SYM", "");
                        logger.debug("New value for " + name + " is " + value);
                        PrimitiveStatement<?> p = getStatement(newTest, name);
                        assert (p != null);
                        if (p instanceof BooleanPrimitiveStatement bp) {
                            bp.setValue(value.intValue() > 0);
                        } else if (p instanceof CharPrimitiveStatement cp) {
                            cp.setValue((char) value.intValue());
                        } else if (p instanceof BytePrimitiveStatement bp) {
                            bp.setValue((byte) value.intValue());
                        } else if (p instanceof ShortPrimitiveStatement sp) {
                            sp.setValue((short) value.intValue());
                        } else if (p instanceof LongPrimitiveStatement lp) {
                            lp.setValue(value);
                        } else {
                            assert (p instanceof IntPrimitiveStatement);
                            IntPrimitiveStatement ip = (IntPrimitiveStatement) p;
                            ip.setValue(value.intValue());
                        }
                    } else {
                        logger.debug("New value is not long " + val);
                    }
                } else {
                    logger.debug("New value is null");

                }
            }
            return newTest;
        } else {
            logger.debug("Got null :-(");
            return null;
        }
    }

    /**
     * Get the statement that defines this variable
     *
     * @param test
     * @param name
     * @return
     */
    private static PrimitiveStatement<?> getStatement(TestCase test, String name) {
        for (Statement statement : test) {
            if (statement instanceof PrimitiveStatement<?> primitiveStatement) {
                if (statement.getReturnValue().getName().equals(name))
                    return primitiveStatement;
            }
        }
        return null;
    }

    /**
     * Apply cone of influence reduction to constraints with respect to the last
     * constraint in the list
     *
     * @param constraints
     * @return
     */
    private static List<Constraint<?>> reduce(List<Constraint<?>> constraints) {

        Constraint<?> target = constraints.get(constraints.size() - 1);
        Set<Variable<?>> dependencies = getVariables(target);

        LinkedList<Constraint<?>> coi = new LinkedList<>();
        coi.add(target);

        for (int i = constraints.size() - 2; i >= 0; i--) {
            Constraint<?> constraint = constraints.get(i);
            Set<Variable<?>> variables = getVariables(constraint);
            for (Variable<?> var : dependencies) {
                if (variables.contains(var)) {
                    dependencies.addAll(variables);
                    coi.addFirst(constraint);
                    break;
                }
            }
        }
        return coi;
    }

    /**
     * Determine the set of variable referenced by this constraint
     *
     * @param constraint
     * @return
     */
    private static Set<Variable<?>> getVariables(Constraint<?> constraint) {
        Set<Variable<?>> variables = new HashSet<>();
        getVariables(constraint.getLeftOperand(), variables);
        getVariables(constraint.getRightOperand(), variables);
        return variables;
    }

    /**
     * Recursively determine constraints in expression
     *
     * @param expr
     * @param variables
     */
    private static void getVariables(Expression<?> expr, Set<Variable<?>> variables) {
        if (expr instanceof Variable<?> variable) {
            variables.add(variable);
        } else if (expr instanceof BinaryExpression<?> bin) {
            getVariables(bin.getLeftOperand(), variables);
            getVariables(bin.getRightOperand(), variables);
        } else if (expr instanceof UnaryExpression<?> un) {
            getVariables(un.getOperand(), variables);
        } else if (expr instanceof Constraint<?>) {
            // ignore

        }
    }
}

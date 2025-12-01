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
package org.evosuite.testcase.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

/**
 * Used to wrap exceptions thrown in code under test. This is needed as VariableReference.getObjects/.setObject
 * and AbstractStatement.execute() do not operate on the same layer.
 * <p>
 * With the introduction of FieldReferences VariableReferences can throw arbitrary (of course wrapped) exceptions,
 * as a Field.get() can trigger static{} blocks
 *
 * @author Sebastian Steenbuck
 */
public class CodeUnderTestException extends Exception {


    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CodeUnderTestException.class);

    /**
     * <p>Constructor for CodeUnderTestException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public CodeUnderTestException(Throwable cause) {
        super(cause);
    }

    /**
     * Used by code calling VariableReference.setObject/2 and .getObject()/1
     *
     * @param e a {@link java.lang.Throwable} object.
     * @return only there to make the compiler happy, this method always throws an exception
     * @throws java.lang.IllegalAccessException      if any.
     * @throws java.lang.IllegalArgumentException    if any.
     * @throws java.lang.NullPointerException        if any.
     * @throws java.lang.ExceptionInInitializerError if any.
     * @throws AssertionError                        if e wasn't one of listed for types
     */
    @Deprecated
    public static Error throwException(Throwable e) throws IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (e instanceof CodeUnderTestException) {
            e = e.getCause();
        }
        if (e instanceof IllegalAccessException exception3) {
            throw exception3;
        } else if (e instanceof IllegalArgumentException exception2) {
            throw exception2;
        } else if (e instanceof NullPointerException exception1) {
            throw exception1;
        } else if (e instanceof ArrayIndexOutOfBoundsException exception) {
            throw exception;
        } else if (e instanceof ExceptionInInitializerError error) {
            throw error;
        } else {
            logger.error("We expected the exception to be one of the listed but it was ", e);
            throw new AssertionError("We expected the exception to be one of the listed but it was " + e.getClass());
        }
    }
}

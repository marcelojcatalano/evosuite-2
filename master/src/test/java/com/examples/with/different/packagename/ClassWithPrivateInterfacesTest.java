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
package com.examples.with.different.packagename;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Snippet from Lang project
 * (org.apache.commons.lang3.ClassUtilsTest)
 */
public class ClassWithPrivateInterfacesTest {

    private interface IA {
    }

    private interface IB {
    }

    private interface IC extends ID, IE {
    }

    private interface ID {
    }

    private interface IE extends IF {
    }

    private interface IF {
    }

    private static class CX implements IB, IA, IE {
    }

    private static class CY extends CX implements IB, IC {
    }

    @Test
    public void testGetAllInterfaces() {
        final List<Class<?>> list = ClassWithPrivateInterfaces.getAllInterfaces(CY.class);

        assertEquals(6, list.size());
        assertSame(IB.class, list.get(0));
        assertSame(IC.class, list.get(1));
        assertSame(ID.class, list.get(2));
        assertSame(IE.class, list.get(3));
        assertSame(IF.class, list.get(4));
        assertSame(IA.class, list.get(5));

        assertNull(ClassWithPrivateInterfaces.getAllInterfaces(null));
    }
}

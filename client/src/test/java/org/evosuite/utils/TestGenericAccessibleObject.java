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

package org.evosuite.utils;

import com.examples.with.different.packagename.generic.ConcreteGenericClass;
import com.examples.with.different.packagename.generic.GuavaExample4;
import com.googlecode.gentyref.TypeToken;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.assertion.Assertion;
import org.evosuite.assertion.Inspector;
import org.evosuite.assertion.InspectorAssertion;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.numeric.IntPrimitiveStatement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericClassFactory;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon Fraser
 */
public class TestGenericAccessibleObject {

    @Test
    public void testGenericMethod() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericMethod.class;
        Method targetMethod = targetClass.getMethod("coverMe",
                Object.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        Assert.assertFalse(genericMethod.getOwnerClass().hasTypeVariables());

        List<GenericClass<?>> parameters = genericMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertTrue(parameters.get(0).hasWildcardTypes());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
        parameters = instantiatedMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertFalse(parameters.get(0).hasWildcardTypes());
    }

    @Test
    public void testGenericMethodWithBounds() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericMethodWithBounds.class;
        Method targetMethod = targetClass.getMethod("is",
                Comparable.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        Assert.assertFalse(genericMethod.getOwnerClass().hasTypeVariables());

        List<GenericClass<?>> parameters = genericMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertTrue(parameters.get(0).hasWildcardTypes());
        Assert.assertTrue(genericMethod.getGeneratedClass().hasWildcardTypes());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
        parameters = instantiatedMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertFalse(parameters.get(0).hasWildcardTypes());
        Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardTypes());
    }

    @Test
    public void testGenericMethodAlternativeBounds() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericMethodAlternativeBounds.class;
        Method targetMethod = targetClass.getMethod("create",
                Class.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        Assert.assertFalse(genericMethod.getOwnerClass().hasTypeVariables());

        List<GenericClass<?>> parameters = genericMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertTrue(parameters.get(0).hasWildcardTypes());
        Assert.assertTrue(genericMethod.getGeneratedClass().hasWildcardTypes());

        // Cannot instantiate because it requires inheritance tree to set up
        // TODO
        // GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
        // parameters = instantiatedMethod.getParameterClasses();
        // Assert.assertFalse(parameters.get(0).hasTypeVariables());
        // Assert.assertFalse(parameters.get(0).hasWildcardTypes());
        // Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardTypes());
    }

    @Test
    public void testGenericClassWithGenericMethodAndSubclass() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericClassWithGenericMethodAndSubclass.class;
        Method targetMethod = targetClass.getMethod("wrap",
                Object.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        Assert.assertTrue(genericMethod.getOwnerClass().hasTypeVariables());
        System.out.println(genericMethod);
        System.out.println(genericMethod.getOwnerClass().toString());
        System.out.println(genericMethod.getGeneratedClass().toString());

        List<GenericClass<?>> parameters = genericMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertTrue(parameters.get(0).hasWildcardTypes());
        Assert.assertTrue(genericMethod.getGeneratedClass().hasWildcardTypes());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
        parameters = instantiatedMethod.getParameterClasses();
        Assert.assertFalse(parameters.get(0).hasTypeVariables());
        Assert.assertFalse(parameters.get(0).hasWildcardTypes());
        Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardTypes());
    }

    @Test
    public void testGenericRawParameter() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericParameters8.class;
        Method targetMethod = targetClass.getMethod("testMe",
                List.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        System.out.println(genericMethod);
        System.out.println(genericMethod.getOwnerClass().toString());
        System.out.println(genericMethod.getGeneratedClass().toString());
        Assert.assertFalse(genericMethod.getOwnerClass().hasTypeVariables());

        List<GenericClass<?>> parameters = genericMethod.getParameterClasses();
        Assert.assertTrue(parameters.get(0).hasTypeVariables());
        Assert.assertFalse(parameters.get(0).hasWildcardTypes());
        Assert.assertFalse(genericMethod.getGeneratedClass().hasWildcardTypes());

		/*
		GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
		parameters = instantiatedMethod.getParameterClasses();
		System.out.println(instantiatedMethod.toString());
		System.out.println(instantiatedMethod.getOwnerClass().toString());
		System.out.println(instantiatedMethod.getGeneratedClass().toString());
		System.out.println(parameters.toString());
		Assert.assertFalse(parameters.get(0).hasTypeVariables());
		Assert.assertFalse(parameters.get(0).hasWildcardTypes());
		Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardTypes());
		*/
    }

    @Test
    public void testLinkedList() throws Exception {
        Class<?> targetClass = java.util.LinkedList.class;
        Method targetMethod = targetClass.getMethod("get", int.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        System.out.println(genericMethod.getGeneratedClass().toString());
        Assert.assertTrue(genericMethod.getGeneratedClass().hasWildcardOrTypeVariables());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiation();
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardOrTypeVariables());
    }

    @Test
    public void testGuavaExample3() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GuavaExample3.class;

        GenericClass<?> genericInstantiation = GenericClassFactory.get(
                new TypeToken<com.examples.with.different.packagename.generic.GuavaExample3<String, String, Object>>() {
                }.getType());

        Method targetMethod = targetClass.getMethod("create",
                com.examples.with.different.packagename.generic.GuavaExample3.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);
        System.out.println(genericMethod.getGeneratedClass().toString());
        Assert.assertTrue(genericMethod.getGeneratedClass().hasWildcardOrTypeVariables());

        System.out.println("------------------");
        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(genericInstantiation);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertFalse(instantiatedMethod.getGeneratedClass().hasWildcardOrTypeVariables());
        Assert.assertEquals(genericInstantiation, instantiatedMethod.getGeneratedClass());
    }

    @Test
    public void testGenericMethodFromReturnValue() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericMethodWithBounds.class;
        Method targetMethod = targetClass.getMethod("is",
                Comparable.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);

        GenericClass<?> generatedType = GenericClassFactory.get(
                new TypeToken<java.util.List<Integer>>() {
                }.getType());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(generatedType);
        Assert.assertEquals(instantiatedMethod.getGeneratedClass(), generatedType);
    }

    @Test
    public void testGenericMethodFromReturnValueWithSubclass() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericClassWithGenericMethodAndSubclass.class;
        Method targetMethod = targetClass.getMethod("wrap",
                Object.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);

        GenericClass<?> generatedType = GenericClassFactory.get(
                new TypeToken<com.examples.with.different.packagename.generic.GenericClassWithGenericMethodAndSubclass.Foo<String>>() {
                }.getType());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(generatedType);
        Assert.assertEquals(instantiatedMethod.getGeneratedClass().getParameterTypes().get(0),
                String.class);
    }

    @Test
    public void testGenericMethodFromReturnValueTypeVariable() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericMethodReturningTypeVariable.class;
        Method targetMethod = targetClass.getMethod("get",
                Object.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);

        GenericClass<?> generatedType1 = GenericClassFactory.get(Integer.class);
        GenericClass<?> generatedType2 = GenericClassFactory.get(String.class);

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(generatedType2);
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), String.class);

        instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(generatedType1);
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), Integer.class);
    }

    @Test
    public void testGenericMethodFromReturnValueTypeVariable2() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GuavaExample4.class;
        Method targetMethod = targetClass.getMethod("create");
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);

        GenericClass<?> iterableIntegerClass = GenericClassFactory.get(
                new TypeToken<com.examples.with.different.packagename.generic.GuavaExample4<java.lang.Iterable<Integer>>>() {
                }.getType());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(iterableIntegerClass);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), GuavaExample4.class);
    }

    @Test
    public void testGenericMethodAbstractType() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.ConcreteGenericClass.class;
        Method targetMethod = targetClass.getMethod("create",
                int.class);
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass);

        Assert.assertSame(genericMethod.getGeneratedClass().getRawClass(), ConcreteGenericClass.class);

        GenericClass<?> iterableIntegerClass = GenericClassFactory.get(
                new TypeToken<com.examples.with.different.packagename.generic.AbstractGenericClass<java.lang.Integer>>() {
                }.getType());

        GenericMethod instantiatedMethod = genericMethod.getGenericInstantiationFromReturnValue(iterableIntegerClass);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), ConcreteGenericClass.class);

        instantiatedMethod = genericMethod.copyWithOwnerFromReturnType(iterableIntegerClass);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), ConcreteGenericClass.class);

        instantiatedMethod = genericMethod.getGenericInstantiation(iterableIntegerClass);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), ConcreteGenericClass.class);

        instantiatedMethod = genericMethod.copyWithNewOwner(iterableIntegerClass);
        System.out.println(instantiatedMethod.getGeneratedClass().toString());
        Assert.assertSame(instantiatedMethod.getGeneratedClass().getRawClass(), ConcreteGenericClass.class);

    }

    @Test
    public void testClassLoaderChange() throws Exception {
        Class<?> targetClass = com.examples.with.different.packagename.generic.GenericClassTwoParameters.class;
        Method creatorMethod = targetClass.getMethod("create");
        Method targetMethod = targetClass.getMethod("get",
                Object.class);
        Method inspectorMethod = targetClass.getMethod("testMe");
        Constructor<?> intConst = Integer.class.getConstructor(int.class);

        GenericClass<?> listOfInteger = GenericClassFactory.get(
                new TypeToken<com.examples.with.different.packagename.generic.GenericClassTwoParameters<Integer, Integer>>() {
                }.getType());

        GenericMethod genericCreatorMethod = new GenericMethod(creatorMethod, targetClass).getGenericInstantiationFromReturnValue(listOfInteger);
        System.out.println(genericCreatorMethod.getGeneratedClass().toString());
        GenericMethod genericMethod = new GenericMethod(targetMethod, targetClass).copyWithNewOwner(genericCreatorMethod.getGeneratedClass());
        System.out.println(genericMethod.getGeneratedClass().toString());

        DefaultTestCase test = new DefaultTestCase();
        MethodStatement ms1 = new MethodStatement(test, genericCreatorMethod,
                null, new ArrayList<>());
        test.addStatement(ms1);

        IntPrimitiveStatement ps1 = (IntPrimitiveStatement) PrimitiveStatement.getPrimitiveStatement(test,
                int.class);
        test.addStatement(ps1);

        GenericConstructor intConstructor = new GenericConstructor(intConst,
                Integer.class);
        List<VariableReference> constParam = new ArrayList<>();
        constParam.add(ps1.getReturnValue());
        ConstructorStatement cs1 = new ConstructorStatement(test, intConstructor,
                constParam);
        //test.addStatement(cs1);

        List<VariableReference> callParam = new ArrayList<>();
        callParam.add(ps1.getReturnValue());

        MethodStatement ms2 = new MethodStatement(test, genericMethod,
                ms1.getReturnValue(), callParam);
        test.addStatement(ms2);

        Inspector inspector = new Inspector(targetClass, inspectorMethod);
        Assertion assertion = new InspectorAssertion(inspector, ms2,
                ms1.getReturnValue(), 0);
        ms2.addAssertion(assertion);

        String code = test.toCode();

        ClassLoader loader = new InstrumentingClassLoader();
        Properties.TARGET_CLASS = targetClass.getCanonicalName();
        Properties.CRITERION = new Criterion[1];
        Properties.CRITERION[0] = Criterion.MUTATION;

        DefaultTestCase testCopy = test.clone();
        testCopy.changeClassLoader(loader);
        String code2 = testCopy.toCode();

        Assert.assertEquals(code, code2);
        Assert.assertEquals(code, test.toCode());

        testCopy.removeAssertion(assertion);
        Assert.assertEquals(code, test.toCode());

        //test.removeAssertion(assertion);
        test.removeAssertions();
        System.out.println(test.toCode());
    }
}

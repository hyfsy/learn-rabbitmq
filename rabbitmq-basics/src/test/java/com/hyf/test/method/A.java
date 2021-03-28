package com.hyf.test.method;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author baB_hyf
 * @date 2021/03/08
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class A {

    public static void main(String[] args) {
        Class<?>[] personArray = new Class<?>[] {IPerson.class};
        Class<?>[] genericPersonArray = new Class<?>[] {IGenericPerson.class};
        IPerson person = (IPerson)Proxy.newProxyInstance(IPerson.class.getClassLoader(), personArray, new AllPersonInvocationHandler());
        IGenericPerson genericPerson = (IGenericPerson)Proxy.newProxyInstance(IGenericPerson.class.getClassLoader(), genericPersonArray, new AllPersonInvocationHandler());

        person.speak("person speak");
        genericPerson.speak(new Object());
    }

    static class AllPersonInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println(method);
            // return method.invoke(proxy, args);
            System.out.println(method.isBridge());
            System.out.println(Integer.toBinaryString(method.getModifiers()));
            return null;
        }
    }

    interface IPerson {
        String speak(String msg);
    }

    interface IGenericPerson<T> {
        T speak(T msg);
    }

    static class Person implements IPerson {
        @Override
        public String speak(String msg) {
            System.out.println("说话: " + msg);
            return msg;
        }
    }
    static class GenericPerson<T> implements IGenericPerson<T> {
        @Override
        public T speak(T msg) {
            System.out.println("说话: " + msg);
            return msg;
        }
    }
}

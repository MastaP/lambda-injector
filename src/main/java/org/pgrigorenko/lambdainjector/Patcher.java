package org.pgrigorenko.lambdainjector;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author Pavel Grigorenko
 */
public class Patcher implements ClassFileTransformer {
  public byte[] transform(ClassLoader cl, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    if (className == null) {
      return null;
    }

    className = className.replace('/', '.');
    if ("org.springframework.web.servlet.FrameworkServlet".equals(className)) {
      System.err.println("FrameworkServlet cl: " + cl);
//      final ClassPool cp = ClassPool.getDefault();
      final ClassPool cp = new ClassPool();
      cp.appendClassPath(new LoaderClassPath(cl));
      cp.appendClassPath(new ByteArrayClassPath(className, classfileBuffer));
      cp.importPackage("pl.joegreen.lambdaFromString");
      cp.importPackage("org.pgrigorenko.lambdainjector.handler");
      try {
        final CtClass ctClass = cp.get(className);
        ctClass.defrost();

        //patch here
        final CtMethod method = CtNewMethod.make("private static void __lambdaInjector__handleRequest(javax.servlet.http.HttpServletRequest request) {\n" +
                "    final String lambdaInjector = request.getHeader(\"LambdaInjector\");\n" +
                "    if (lambdaInjector != null) {\n" +
                "      org.pgrigorenko.lambdainjector.handler.LambdaRunner.fromString(lambdaInjector);" +
                "    }\n" +
                "    else {\n" +
                "      System.err.println(\"In __lambdaInjector__handleRequest\");\n" +
                "    }\n" +
                "  }", ctClass);
        ctClass.addMethod(method);
        final CtMethod serviceMethod = ctClass.getDeclaredMethod("service", cp.get(new String[]{"javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse"}));
        serviceMethod.insertBefore(
                "{" +
//                "  System.err.println(\"\\nInside service()\");\n" +
//                "  System.err.println($1);\n" +
//                "  System.err.println($1.getClass().getName());\n" +
//                "  System.err.println($1.getClass().getClassLoader());" +
//                "  System.err.println(\"Interfaces: \" + java.util.Arrays.toString($1.getClass().getInterfaces()));" +
//                "  final ClassLoader classLoader = this.getClass().getClassLoader();\n" +
//                "  System.err.println(classLoader);\n" +
//                "  System.err.println(Thread.currentThread().getName());" +
//                "  Thread.dumpStack();\n" +
//                "  try {\n" +
//                "    final Class aClass = Class.forName(\"javax.servlet.http.HttpServletRequest\", false, classLoader);\n" +
//                "    System.err.println(\"forName: \" + aClass);\n" +// + " " + aClass
//                "  } catch (Throwable t) {\n" +
//                "    t.printStackTrace();\n" +
//                "  }" +
                "  System.err.println(\"Before handleRequest()\");\n" +
//                "  //System.err.println(org.pgrigorenko.lambdainjector.HttpServletRequestHandler.class.getName());" +
//                "  //System.err.println(org.pgrigorenko.lambdainjector.HttpServletRequestHandler.class.getClassLoader());" +
                "  try {" +
                "    __lambdaInjector__handleRequest($1);" +
                "  } catch (Throwable t) {" +
                "    System.err.println(\"Something bad has happened: \" + t.getMessage());" +
                "    t.printStackTrace();\n" +
                "  }" +
                "}");
        return ctClass.toBytecode();
      }
      catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
    return null;
  }
}

package org.pgrigorenko.lambdainjector;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Pavel Grigorenko
 */
public class Patcher implements ClassFileTransformer {

  private final static Set<String> patchedClasses = Collections.unmodifiableSet(new HashSet<String>() {
    {
      add("javax.servlet.http.HttpServlet");
      add("org.springframework.web.servlet.FrameworkServlet");
    }
  });

  public byte[] transform(ClassLoader cl, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    if (className == null) {
      return null;
    }

    className = className.replace('/', '.');

    if (patchedClasses.contains(className)) {
      final ClassPool cp = new ClassPool();
      cp.appendClassPath(new LoaderClassPath(cl));
      cp.appendClassPath(new ByteArrayClassPath(className, classfileBuffer));
      cp.importPackage("pl.joegreen.lambdaFromString");
      cp.importPackage("org.pgrigorenko.lambdainjector.handler");
      try {
        final CtClass ctClass = cp.get(className);
        ctClass.defrost();

        final CtMethod method = CtNewMethod.make(
                "  private static void __lambdaInjector__handleRequest(javax.servlet.http.HttpServletRequest request) {" +
                "    final String lambdaInjector = request.getHeader(org.pgrigorenko.lambdainjector.handler.Headers.LambdaInjectorRunnableBody.name());" +
                "    if (lambdaInjector != null) {" +
                "      org.pgrigorenko.lambdainjector.handler.LambdaRunner.executeRunnableBody(lambdaInjector);" +
                "    }" +
                "  }", ctClass);
        ctClass.addMethod(method);

        final CtMethod serviceMethod = ctClass.getDeclaredMethod("service", cp.get(new String[]{"javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse"}));
        serviceMethod.insertBefore(
                "{" +
                "  try {" +
                "    __lambdaInjector__handleRequest($1);" +
                "  } catch (Throwable t) {" +
                "    System.err.println(\"Something bad has happened: \" + t.getMessage());" +
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

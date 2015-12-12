package org.pgrigorenko.lambdainjector.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pavel Grigorenko
 */
public class HttpServletRequestHandler {

  public static void __lambdaInjector__handleRequest(HttpServletRequest request) {
    final String lambdaInjector = request.getHeader("LambdaInjector");
    if (lambdaInjector != null) {
      LambdaRunner.fromString(lambdaInjector);
    }
    else {
      System.err.println("In __lambdaInjector__handleRequest");
    }
  }
}

package org.pgrigorenko.lambdainjector.handler;

/**
 * @author Pavel Grigorenko
 */
public class HttpServletRequestHandler {

  public static void handleRequest(javax.servlet.http.HttpServletRequest request) {
    final String runnableBody = request.getHeader(Headers.lambda_runnable_body.name());
    if (runnableBody != null) {
      LambdaRunner.executeRunnableBody(runnableBody);
    }
  }
}

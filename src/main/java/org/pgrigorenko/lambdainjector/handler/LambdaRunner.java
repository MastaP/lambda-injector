package org.pgrigorenko.lambdainjector.handler;

import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

/**
 * @author Pavel Grigorenko
 */
public class LambdaRunner {

  public static void fromString(String lambda) {
    System.err.println("\n\nGoing to run the following lambda: " + lambda);
    //TODO encode/decode base64
    final LambdaFactory lambdaFactory = LambdaFactory.get();
    final Runnable runnable = lambdaFactory.createLambdaUnchecked(lambda, new TypeReference<Runnable>() {});
    runnable.run();
  }
}

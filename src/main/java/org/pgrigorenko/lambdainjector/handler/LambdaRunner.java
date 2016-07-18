package org.pgrigorenko.lambdainjector.handler;

import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

/**
 * @author Pavel Grigorenko
 */
public class LambdaRunner {

  private static final String FORMAT_RUNNABLE = "() -> { %s }";
  private static final ThreadLocal<Boolean> isRunning = ThreadLocal.withInitial(() -> Boolean.FALSE);

  public static void executeRunnableBody(String body) {
    if (isRunning.get()) {
      return;
    }
    isRunning.set(true);
    //TODO encode/decode base64
    final LambdaFactory lambdaFactory = LambdaFactory.get();
    final Runnable runnable =
            lambdaFactory.createLambdaUnchecked(
                    String.format(FORMAT_RUNNABLE, body),
                    new TypeReference<Runnable>() {});
    runnable.run();
  }

  public static void cleanup() {
    isRunning.remove();
  }
}

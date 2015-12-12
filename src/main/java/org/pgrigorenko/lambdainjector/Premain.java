package org.pgrigorenko.lambdainjector;

import java.lang.instrument.Instrumentation;

/**
 * @author Pavel Grigorenko
 */
public class Premain {

  public static void premain(String agentArgs, Instrumentation instr) {
    System.err.println("Injector starting");
    instr.addTransformer(new Patcher());
  }
}

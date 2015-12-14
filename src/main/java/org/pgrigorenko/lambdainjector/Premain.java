package org.pgrigorenko.lambdainjector;

import java.lang.instrument.Instrumentation;

/**
 * @author Pavel Grigorenko
 */
public class Premain {

  public static void premain(String agentArgs, Instrumentation instr) {
    instr.addTransformer(new Patcher());
  }
}

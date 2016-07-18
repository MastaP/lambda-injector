package org.pgrigorenko.lambdainjector;

import java.lang.instrument.Instrumentation;

/**
 * @author Pavel Grigorenko
 */
public class Premain {

  public static void premain(String agentArgs, Instrumentation instr) {
    System.out.print(
          "******************************\n"
        + "** Lambda Injector Starting **\n"
        + "******************************\n"
    );
    instr.addTransformer(new Patcher());
  }
}

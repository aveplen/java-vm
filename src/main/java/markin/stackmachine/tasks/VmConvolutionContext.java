package markin.stackmachine.tasks;

import markin.stackmachine.compiler.Compiler;

public class VmConvolutionContext {

  private static String program = """
     start:
     push
     0
     load

     dup
     push
     79
     stor

     dup
     push
     &finalyze
     swap
     jz

     stc

     mult_routine:
     cts
     load

     cts
     push
     79
     load
     push
     1
     add
     add
     load


     mul

     cdec

     cts
     push
     &sum_routine
     swap
     jz

     push
     &mult_routine
     jmp

     sum_routine:
     push
     &arr_len
     load
     stc
     cdec

     while:
     add
     cdec

     cts
     push
     &finalyze
     swap
     jz

     push
     &while
     jmp

     finalyze:
     term
      """;

  private static int[] code;

  static {
    code = Compiler.compileFromString(program, false);
  }

  public static int[] getCode() {
    return code;
  }
}

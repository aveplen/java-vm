package markin.stackmachine.vm;

import static markin.stackmachine.vm.Instruction.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CpuTest {

  private final int[] instructions = new int[]{
      /* 00 */ PUSH.ordinal(),
      /* 01 */ 0,
      /* 02 */ LOAD.ordinal(),
      /* 03 */ DUP.ordinal(),

      /* 04 */ PUSH.ordinal(),
      /* 05 */ 35, // &finalize
      /* 06 */ SWAP.ordinal(),
      /* 07 */ JZ.ordinal(),

      /* 08 */ STC.ordinal(),

      //       while_1:
      /* 09 */ CTS.ordinal(),
      /* 10 */ LOAD.ordinal(),
      /* 11 */ CDEC.ordinal(),
      /* 12 */ CTS.ordinal(),
      /* 13 */ PUSH.ordinal(),
      /* 14 */ 20,// &sum
      /* 15 */ SWAP.ordinal(),
      /* 16 */ JZ.ordinal(),
      /* 17 */ PUSH.ordinal(),
      /* 18 */ 9,// &while_1
      /* 19 */ JMP.ordinal(),

      //       sum:
      /* 20 */ PUSH.ordinal(),
      /* 21 */ 0,
      /* 22 */ LOAD.ordinal(),
      /* 23 */ STC.ordinal(),

      /* 24 */ CDEC.ordinal(),

      //       while_2:
      /* 25 */ CTS.ordinal(),
      /* 26 */ PUSH.ordinal(),
      /* 27 */ 35, // &finalize
      /* 28 */ SWAP.ordinal(),
      /* 29 */ JZ.ordinal(),

      /* 30 */ ADD.ordinal(),
      /* 31 */ CDEC.ordinal(),
      /* 32 */ PUSH.ordinal(),
      /* 33 */ 25, // &while_2
      /* 34 */ JMP.ordinal(),

      //       finalize:
      /* 35 */ TERM.ordinal(),
  };

  private final int[] data = new int[]{
    4, 10, 15, 20, 25
  };

  private final int expected = 70;

  @Test
  public void run() {
    Cpu cpu = new Cpu(instructions, data);
    cpu.run();
    assertEquals(expected, cpu.getStack()[0]);
  }
}
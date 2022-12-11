package markin.stackmachine.compiler;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.stream.Stream;
import markin.stackmachine.vm.Instruction;
import org.junit.Test;

public class CompilerTest {

  private final Iterator<Token> tokens = Stream.of(
      /* skip */ new Token("// this is a test", TokenType.COMMENT),
      /*   0  */ new Token("pUsH", TokenType.INSTRUCTION),
      /*   1  */ new Token("0", TokenType.INTEGER),
      /*   2  */ new Token("&future_label", TokenType.LABELREF),
      /* skip */ new Token("/* some junk */", TokenType.COMMENT),
      /*   3  */ new Token("future_label:", TokenType.LABEL),
      /* skip */ new Token("/* some more junk */", TokenType.COMMENT),
      /*   4  */ new Token("past_label:", TokenType.LABEL),
      /*   5  */ new Token("&past_label", TokenType.LABELREF)
  ).iterator();

  private final String strings = """
    // this is a test
    pUsH
    0
    &future_label
    /* some junk */
    future_label:
    /* some more junk */
    past_label:
    &past_label
    """;

  private final int[] expected = new int[]{
      Instruction.encode(Instruction.PUSH),
      0,
      3,
      Instruction.encode(Instruction.NOOP),
      Instruction.encode(Instruction.NOOP),
      4
  };

  @Test
  public void compileFromTokenIterator() {
    int[] compiled = Compiler.compileFromTokenIterator(tokens, false);
    assertArrayEquals(expected, compiled);
  }

  @Test
  public void compileFromStringIterator() {
    int[] compiled = Compiler.compileFromString(strings, false);
    assertArrayEquals(expected, compiled);
  }
}
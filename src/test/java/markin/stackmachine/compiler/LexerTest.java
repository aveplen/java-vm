package markin.stackmachine.compiler;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class LexerTest {

  private final String program = """
/* 01 */   push // load array length from memory 0x00
/* 02 */   0
           some_label:
           &some_reference
      """;

  private final List<Token> expected = List.of(
      new Token("/* 01 */", TokenType.COMMENT),
      new Token("push", TokenType.INSTRUCTION),
      new Token("// load array length from memory 0x00", TokenType.COMMENT),
      new Token("/* 02 */", TokenType.COMMENT),
      new Token("0", TokenType.INTEGER),
      new Token("some_label:", TokenType.LABEL),
      new Token("&some_reference", TokenType.LABELREF)
  );

  @Test
  public void test() {

    Iterator<Character> charIterator = program.chars()
        .mapToObj(c -> (char)c)
        .collect(Collectors.toList())
        .iterator();

    List<Token> tokens = new LinkedList<>();
    Lexer lexer = new Lexer(charIterator);
    while(lexer.hasNext()) {
      tokens.add(lexer.next());
    }

    assertEquals(expected, tokens);
  }
}
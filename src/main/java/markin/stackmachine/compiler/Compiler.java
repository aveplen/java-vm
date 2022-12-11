package markin.stackmachine.compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import markin.stackmachine.vm.Instruction;

public class Compiler {

  private final Iterator<Token> tokenIterator;
  private final List<Integer> buffer;
  private final Map<String, Integer> labelMapping;
  private final List<LabelReference> deferredLabelRefs;
  private boolean verbose;
  private int instructionCounter;

  public static int[] compileFromTokenIterator(Iterator<Token> tokenIterator, boolean verbose) {
    return new Compiler(tokenIterator, verbose).compile();
  }

  public static int[] compileFromString(String program, boolean verbose) {
    Lexer lexer = new Lexer(program.chars().mapToObj(c -> (char)c).iterator());
    return new Compiler(lexer, verbose).compile();
  }

  private Compiler(Iterator<Token> tokenIterator, boolean verbose) {
    this.verbose = verbose;
    this.tokenIterator = tokenIterator;
    buffer = new LinkedList<>();
    labelMapping = new HashMap<>();
    deferredLabelRefs = new LinkedList<>();
    this.instructionCounter = 0;
  }

  private int[] compile() {
    while (tokenIterator.hasNext()) {
      Token token = tokenIterator.next();

      if (token.getType().equals(TokenType.COMMENT)) {
        continue;
      }

      if (verbose) {
        System.out.printf("%d: %s\n", instructionCounter, token);
      }

      TokenType type = token.getType();
      String value = token.getValue();

      int append = switch (type) {
        case INTEGER -> compileInteger(value);
        case LABEL -> compileLabel(value);
        case LABELREF -> compileLabelRef(value);
        case INSTRUCTION -> compileInstruction(value);
        case COMMENT -> 0;
      };

      buffer.add(append);
      instructionCounter++;
    }

    resolveLabelReferences();

    int[] ret = new int[buffer.size()];
    for (int i = 0; i < buffer.size(); i++) {
      ret[i] = buffer.get(i);
    }
    return ret;
  }

  private int compileInstruction(String value) {
    return Instruction.encode(value.toUpperCase());
  }

  private int compileInteger(String value) {
    return Integer.parseInt(value);
  }

  private int compileLabel(String value) {
    String label = value.substring(0, value.length() - 1);

    if (labelMapping.containsKey(label)) {
      throw new RuntimeException("attempt to override label " + label);
    }

    labelMapping.put(label, instructionCounter);
    return Instruction.encode(Instruction.NOOP);
  }

  private int compileLabelRef(String value) {
    String label = value.substring(1);

    if (!labelMapping.containsKey(label)) {
      deferredLabelRefs.add(new LabelReference(label, instructionCounter));
      return 0;
    }

    return labelMapping.get(label);
  }

  private void resolveLabelReferences() {
    for (LabelReference labelRef : deferredLabelRefs) {
      if (!labelMapping.containsKey(labelRef.getName())) {
        throw new RuntimeException("unresolved back label reference " + labelRef.getName());
      }

      buffer.set(labelRef.getAt(), labelMapping.get(labelRef.getName()));
    }
  }
}

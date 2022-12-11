package markin.stackmachine.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class Lexer implements Iterator<Token> {

  private final Map<LexerState, TokenType> lexerStateToTokenType = Map.of(
      LexerState.NUMBER, TokenType.INTEGER,
      LexerState.HEXORBINNUMBER, TokenType.INTEGER,
      LexerState.HEXNUMBER, TokenType.INTEGER,
      LexerState.BINNUMBER, TokenType.INTEGER,
      LexerState.LABELREF, TokenType.LABELREF,
      LexerState.INSTRUCTION, TokenType.INSTRUCTION,
      LexerState.LABEL, TokenType.LABEL,
      LexerState.COMMENTMLCLOSING, TokenType.COMMENT,
      LexerState.COMMENTSL, TokenType.COMMENT
  );

  private Iterator<Character> charIterator;
  private Iterator<Iterator<Character>> charIterators;
  private LexerState state;
  private Token outbox;
  private List<Character> buffer;
  private boolean ready;
  private boolean exhausted;
  private boolean closed;

  public Lexer(Iterator<Character> charIterator) {
    this.charIterator = charIterator;
    this.state = LexerState.INITIAL;
    this.outbox = null;
    this.buffer = new LinkedList<>();
    this.ready = false;
    this.exhausted = false;
    this.closed = false;

    walk();
  }

  public Lexer(Iterable<Character> characters) {
    this(characters.iterator());
  }

  @Override
  public boolean hasNext() {
    return !closed;
  }

  @Override
  public Token next() {
    Token current = outbox;
    ready = false;
    walk();
    return current;
  }

  private void walk() {
    if (exhausted) {
      closed = true;
    }

    while (charIterator.hasNext()) {
      char next = charIterator.next();

      state = switch (state) {
        case INITIAL -> initial(next);
        case NUMBER -> number(next);
        case HEXORBINNUMBER -> hbnumber(next);
        case HEXNUMBER -> hnumber(next);
        case BINNUMBER -> bnumber(next);
        case LABELREF -> labelref(next);
        case COMMENT -> comment(next);
        case INSTRUCTION -> instr(next);
        case LABEL -> label(next);
        case COMMENTML -> commentml(next);
        case COMMENTMLCLOSING -> commentmlclosing(next);
        case COMMENTSL -> commentsl(next);
      };

      if (ready) {
        return;
      }
    }

    if (buffer.size() != 0) {
      this.yield();
      exhausted = true;
      return;
    }

    closed = true;
  }

  private void yield() {
    StringBuilder sb = new StringBuilder();
    for (char ch : buffer) {
      sb.append(ch);
    }

    String lexem = sb.toString();

    if (!lexerStateToTokenType.containsKey(state)) {
      throw new RuntimeException(
          "unknown lexer state to token type conversion " + state
      );
    }

    outbox = new Token(lexem, lexerStateToTokenType.get(state));
    buffer.clear();
    ready = true;
  }

  private LexerState initial(char next) {
    if (next == '0') {
      buffer.add(next);
      return LexerState.HEXORBINNUMBER;
    }

    if (digit(next)) {
      buffer.add(next);
      return LexerState.NUMBER;
    }

    if (next == '&') {
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (labelStartChar(next)) {
      buffer.add(next);
      return LexerState.INSTRUCTION;
    }

    if (next == '/') {
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      return LexerState.INITIAL;
    }

    throw new RuntimeException("no next state at INITIAL");
  }

  private LexerState comment(char next) {
    if (next == '/') {
      buffer.add(next);
      return LexerState.COMMENTSL;
    }

    if (next == '*') {
      buffer.add(next);
      return LexerState.COMMENTML;
    }

    throw new RuntimeException("no next state at COMMENT");
  }

  private LexerState commentml(char next) {
    if (next == '*') {
      buffer.add(next);
      return LexerState.COMMENTMLCLOSING;
    }

    buffer.add(next);

    return LexerState.COMMENTML;
  }

  private LexerState commentmlclosing(char next) {
    if (next == '/') {
      buffer.add(next);
      this.yield();
      return LexerState.INITIAL;
    }

    if (next == '*') {
      buffer.add(next);
      return LexerState.COMMENTMLCLOSING;
    }

    buffer.add(next);
    return LexerState.COMMENTML;
  }

  private LexerState  commentsl(char next) {
    if (next == '\n' || next == '\r') {
      this.yield();
      return LexerState.INITIAL;
    }

    buffer.add(next);
    return LexerState.COMMENTSL;
  }

  private LexerState number(char next) {
    if (digit(next) || next == 'b' || next == 'x') {
      buffer.add(next);
      return LexerState.NUMBER;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal number format " + next);
  }

  private LexerState hbnumber(char next) {
    if (next == 'b') {
      buffer.add(next);
      return LexerState.BINNUMBER;
    }

    if (next == 'x') {
      buffer.add(next);
      return LexerState.HEXNUMBER;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal number format " + next);
  }

  private LexerState  hnumber(char next) {
    if (hexDigit(next)) {
      buffer.add(next);
      return LexerState.HEXNUMBER;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal hex digit " + next);
  }

  private LexerState  bnumber(char next) {
    if (binaryDigit(next)) {
      buffer.add(next);
      return LexerState.BINNUMBER;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal binary digit " + next);
  }

  private LexerState  labelref(char next) {
    if (labelStartChar(next) || digit(next)) {
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal labelref character " + next);
  }

  private LexerState instr(char next) {
    if (labelChar(next)) {
      buffer.add(next);
      return LexerState.INSTRUCTION;
    }

    if (next == ':') {
      buffer.add(next);
      state = LexerState.LABEL;
      this.yield();
      return LexerState.INITIAL;
    }

    if (next == '&') {
      this.yield();
      buffer.add(next);
      return LexerState.LABELREF;
    }

    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    if (next == '/') {
      this.yield();
      buffer.add(next);
      return LexerState.COMMENT;
    }

    throw new RuntimeException("illegal character for instruction " + next);
  }

  private LexerState label(char next) {
    if (whitespace(next)) {
      this.yield();
      return LexerState.INITIAL;
    }

    throw new RuntimeException("illegal character for label " + next);
  }

  // TODO: rename to whitespace
  private boolean whitespace(char c) {
    return (c == ' ' || c == '\t' || c == '\r' || c == '\n');
  }

  private boolean digit(char c) {
    return ('0' <= c && c <= '9');
  }

  private boolean hexDigit(char c) {
    return (
        'a' <= c && c <= 'f' ||
        'A' <= c && c <= 'F' ||
        digit(c)
    );
  }

  private boolean binaryDigit(char c) {
    return (c == '0' || c == '1');
  }

  private boolean lowercaseChar(char c) {
    return ('a' <= c && c <= 'z');
  }

  private boolean uppercaseChar(char c) {
    return ('A' <= c && c <= 'Z');
  }

  private boolean labelStartChar(char c) {
    return (c == '_' || lowercaseChar(c) || uppercaseChar(c));
  }

  private boolean labelChar(char c) {
    return (labelStartChar(c) || digit(c));
  }
}

package markin.stackmachine.compiler;

public class Token {

  private String value;
  private TokenType type;

  public Token(String value, TokenType type) {
    this.value = value;
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public TokenType getType() {
    return type;
  }

  @Override
  public String toString() {
    return String.format("{'%s', %s}", value, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (!obj.getClass().equals(this.getClass())) {
      return false;
    }

    Token cast = (Token) obj;
    if (!value.equals(cast.value)) {
      return false;
    }

    return type.equals(cast.type);
  }
}

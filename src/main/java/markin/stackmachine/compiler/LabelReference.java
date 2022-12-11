package markin.stackmachine.compiler;

public class LabelReference {

  private final String name;
  private final int at;

  public LabelReference(String name, int at) {
    this.name = name;
    this.at = at;
  }

  public String getName() {
    return name;
  }

  public int getAt() {
    return at;
  }
}
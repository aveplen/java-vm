package markin.stackmachine.app;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class CompilerAppTest {

  private final String[] args = new String[]{
      "--verbose",
      "-i", "arr_sum.raw",
      "-o", "arr_sum.compiled",
  };

  @Test
  public void debuggingEntryPoint() throws ParseException {
    CompilerApp.main(args);
  }
}
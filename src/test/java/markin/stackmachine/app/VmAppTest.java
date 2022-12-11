package markin.stackmachine.app;

import static org.junit.Assert.*;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class VmAppTest {

  private final String[] args = new String[]{
      "--verbose",
      "-i", "arr_sum.compiled",
      "5", "1", "2", "3", "4", "5"
  };

  @Test
  public void debuggingEntryPoint() throws ParseException {
    VmApp.main(args);
  }
}
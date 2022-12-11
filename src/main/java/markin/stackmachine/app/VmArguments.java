package markin.stackmachine.app;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class VmArguments {
  private boolean verbose;
  private File inputFile;
  private List<Integer> initialData;

  public VmArguments(String[] args) throws ParseException {
    this.initialData = new LinkedList<>();
    this.verbose = false;

    Options options = new Options()
        .addOption(new Option("v", "verbose", false, "verbose"))
        .addRequiredOption("i", "input", true, "input file");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    if (cmd.hasOption("v")) {
      verbose = true;
    }

    String inputFilePath = cmd.getOptionValue("i");
    inputFile = new File(inputFilePath);
    if (!inputFile.exists()) {
      System.out.println("input file must exist");
      System.exit(1);
    }

    for (String arg : cmd.getArgs()) {
      this.initialData.add(Integer.parseInt(arg));
    }
  }

  public boolean isVerbose() {
    return verbose;
  }

  public File getInputFile() {
    return inputFile;
  }

  public List<Integer> getInitialData() {
    return initialData;
  }
}

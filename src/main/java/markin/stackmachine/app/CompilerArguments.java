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

public class CompilerArguments {

  private boolean verbose;
  private File inputFile;
  private File outputFile;

  public CompilerArguments(String[] args) throws ParseException {
    this.verbose = false;

    Options options = new Options()
        .addOption(new Option("v", "verbose", false, "verbose"))
        .addRequiredOption("i", "input", true, "input file")
        .addRequiredOption("o", "output", true, "output file");

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


    String outputFilePath = cmd.getOptionValue("o");
    outputFile = new File(outputFilePath);
  }

  public boolean isVerbose() {
    return verbose;
  }

  public File getInputFile() {
    return inputFile;
  }

  public File getOutputFile() {
    return outputFile;
  }
}

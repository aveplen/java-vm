package markin.stackmachine.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import markin.stackmachine.compiler.Compiler;
import org.apache.commons.cli.ParseException;

public class CompilerApp {

  public static void main(String[] args) throws ParseException {
    CompilerArguments arguments = new CompilerArguments(args);

    StringBuilder sb = new StringBuilder();
    try (
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(arguments.getInputFile())
            ))) {

      int c;
      while ((c = reader.read()) != -1) {
        sb.append((char) c);
      }


    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    int[] compiled = Compiler.compileFromString(
        sb.toString(),
        arguments.isVerbose()
    );

    try (
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(arguments.getOutputFile())
            ))) {

      for (int compiledByte : compiled) {
        writer.write(compiledByte);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

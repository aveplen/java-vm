package markin.stackmachine.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import markin.stackmachine.output.Output;
import markin.stackmachine.vm.Cpu;
import org.apache.commons.cli.ParseException;

public class VmApp {
  public static void main(String[] args) throws ParseException {
    VmArguments arguments = new VmArguments(args);

    List<Integer> program = new LinkedList<>();
    try (
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(arguments.getInputFile())
            )))
    {

      int c;
      while((c = reader.read()) != -1) {
        program.add(c);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Cpu cpu = new Cpu(program, arguments.getInitialData());
    cpu.run();


    if (arguments.isVerbose()) {
      System.out.println(
          Output.buildDashboard(
              cpu.getInstructions(),
              cpu.getData(),
              cpu.getStack(),
              cpu.getCounterReg(),
              cpu.getStackPtr(),
              cpu.getInstructionPtr(),
              cpu.isRunning()
          )
      );
    }
  }
}

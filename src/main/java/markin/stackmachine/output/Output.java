package markin.stackmachine.output;

import markin.stackmachine.vm.Settings;
import org.sk.PrettyTable;

public class Output {

  public static String buildDashboard(
      int[] instructions,
      int[] data,
      int[] stack,
      int counterReg,
      int stackPtr,
      int instructionPtr,
      boolean running
  ) {

    StringBuilder sb = new StringBuilder();
    sb
        .append("Instruction space")
        .append("\n")
        .append(buildTable(instructions))
        .append("\n")
        .append("Data space")
        .append("\n")
        .append(buildTable(data))
        .append("\n")
        .append("Stack")
        .append("\n")
        .append(buildTable(stack))
        .append("\n")
        .append("instruction pointer: " + instructionPtr)
        .append("\n")
        .append("stack pointer: " + stackPtr)
        .append("\n")
        .append("counter register: " + counterReg)
        .append("\n")
        .append("is running: " + running);

    return sb.toString();
  }

  private static String buildTable(int[] data) {
    PrettyTable pt = new PrettyTable(buildHeaders());
    for (int i = 0; i < data.length / Settings.outputWidth; i++) {
      pt.addRow(buildRow(data, i*Settings.outputWidth));
    }
    return pt.toString();
  }

  private static String[] buildHeaders() {
    String[] headers = new String[Settings.outputWidth+1];
    headers[0] = "";
    for (int i = 0; i < Settings.outputWidth; i++) {
      headers[i+1] = String.format("+%d", i);
    }
    return headers;
  }

  private static String[] buildRow(int[] data, int offset) {
    String[] row = new String[Settings.outputWidth+1];
    row[0] = String.format("%d", offset);
    for (int i = 0; i < Settings.outputWidth; i++) {
      row[i+1] = String.format("%d", data[offset + i]);
    }
    return row;
  }
}

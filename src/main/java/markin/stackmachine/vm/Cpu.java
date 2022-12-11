package markin.stackmachine.vm;

import java.util.List;
import java.util.Scanner;

public class Cpu {

  private final int[] stack;
  private final int[] instructions;
  private final int[] data;
  private int counterReg;
  private int stackPtr;
  private int instructionPtr;
  private boolean running;
  private int memorySize;
  private int stackLimit;

  public Cpu() {
    this.stack = new int[Settings.stackLimit];
    this.instructions = new int[Settings.instructionSpaceSize];
    this.data = new int[Settings.dataSpaceSize];
    this.counterReg = 0;
    this.stackPtr = -1;
    this.instructionPtr = 0;
    this.running = true;
    this.memorySize = Settings.dataSpaceSize;
    this.stackLimit = Settings.stackLimit;
  }

  public Cpu(int[] instructions, int[] data) {
    this();
    System.arraycopy(instructions, 0, this.instructions, 0, instructions.length);
    System.arraycopy(data, 0, this.data, 0, data.length);
  }

  public Cpu(int[] instructions, int[] data, int customDataSpaceSize){
    this.stack = new int[customDataSpaceSize];
    this.instructions = new int[customDataSpaceSize];
    this.data = new int[customDataSpaceSize];
    this.counterReg = 0;
    this.stackPtr = -1;
    this.instructionPtr = 0;
    this.running = true;

    this.stackLimit = customDataSpaceSize;
    this.memorySize = customDataSpaceSize;
    System.arraycopy(instructions, 0, this.instructions, 0, instructions.length);
    System.arraycopy(data, 0, this.data, 0, data.length);
  }

  public Cpu(List<Integer> instructions, List<Integer> data) {
    this();

    for (int i = 0; i < instructions.size(); i++) {
      this.instructions[i] = instructions.get(i);
    }

    for (int i = 0; i < data.size(); i++) {
      this.data[i] = data.get(i);
    }
  }

  public void run() {
    while (running) {
      tick();
    }
  }

  public int[] getStack() {
    return stack;
  }

  public int[] getInstructions() {
    return instructions;
  }

  public int[] getData() {
    return data;
  }

  public int getCounterReg() {
    return counterReg;
  }

  public int getStackPtr() {
    return stackPtr;
  }

  public int getInstructionPtr() {
    return instructionPtr;
  }

  public boolean isRunning() {
    return running;
  }

  private void tick() {
    if (!running) {
      throw new RuntimeException("attempt to tick while not running");
    }

    int fetched = fetch();
    Instruction decoded = decode(fetched);
    execute(decoded);
  }

  private int fetch() {
    int cmd = instructions[instructionPtr];
    instructionPtr++;
    return cmd;
  }

  private Instruction decode(int opcode) {
    return Instruction.decode(opcode);
  }

  private void execute(Instruction inst) {
    switch (inst) {
      case NOOP ->  noopInstruction();
      case ADD ->   addInstruction();
      case SUB ->   subInstruction();
      case AND ->   andInstruction();
      case OR ->    orInstruction();
      case XOR ->   xorInstruction();
      case NOT ->   notInstruction();
      case IN ->    inInstruction();
      case LOAD ->  loadInstruction();
      case STOR ->  storInstruction();
      case JMP ->   jmpInstruction();
      case JZ ->    jzInstruction();
      case JNZ ->   jnzInstruction();
      case PUSH ->  pushInstruction();
      case DUP ->   dupInstruction();
      case SWAP ->  swapInstruction();
      case ROLR ->  rolrInstruction();
      case ROLL ->  rollInstruction();
      case DROP ->  dropInstruction();
      case COMP ->  compInstruction();
      case CDEC ->  cdecInstruction();
      case CINC ->  cincInstruction();
      case CTS ->   ctsInstruction();
      case STC ->   stcInstruction();
      case TERM ->  termInstruction();
      case OUT ->   outInstruction();
      case OUTNH -> outnhInstuction();
      case MUL ->   mulInstruction();
    }
  }

  private void terminate() {
    running = false;
  }

  private void push(int x) {
    if (stackPtr == stackLimit-1) {
      throw new RuntimeException("stack overflow");
    }
    stackPtr++;
    stack[stackPtr] = x;
  }

  private int pop() {
    if (stackPtr == -1) {
      throw new RuntimeException("stack underflow");
    }
    int ret = stack[stackPtr];
    stack[stackPtr] = 0;
    stackPtr--;
    return ret;
  }

  private void noopInstruction() {}

  private void addInstruction() {
    push(pop() + pop());
  }

  private void subInstruction() {
    int r = pop();
    int l = pop();
    push(l - r);
  }

  private void andInstruction() {
    push(pop() & pop());
  }

  private void orInstruction() {
    push(pop() | pop());
  }

  private void xorInstruction() {
    push(pop() ^ pop());
  }

  private void notInstruction() {
    push(~pop());
  }

  private void inInstruction() {
    Scanner scanner = new Scanner(System.in);
    push(scanner.nextInt());
    scanner.close();
  }

  private void outInstruction() {
    System.out.printf("%d\n", pop());
  }

  private void loadInstruction() {
    push(data[pop()]);
  }

  private void storInstruction() {
    int addr = pop();
    data[addr] = pop();
  }

  private void jmpInstruction() {
    instructionPtr = pop();
  }

  private void jzInstruction() {
    if (pop() != 0) {
      pop();
      return;
    }
    instructionPtr = pop();
  }

  private void jnzInstruction() {
    if (pop() == 0) {
      pop();
      return;
    }
    instructionPtr = pop();
  }

  private void pushInstruction() {
    push(instructions[instructionPtr++]);
  }

  private void dupInstruction() {
    int a = pop();
    push(a);
    push(a);
  }

  private void swapInstruction() {
    int a = pop();
    int b = pop();
    push(a);
    push(b);
  }

  // stack grow ->
  // c b a => a c b
  private void rolrInstruction() {
    int a = pop();
    int b = pop();
    int c = pop();
    push(a);
    push(c);
    push(b);
  }

  // stack grow ->
  // c b a => b a c
  private void rollInstruction() {
    int a = pop();
    int b = pop();
    int c = pop();
    push(b);
    push(a);
    push(c);
  }

  private void dropInstruction() {
    pop();
  }

  private void compInstruction() {
    push(-pop());
  }

  private void cdecInstruction() {
    counterReg--;
  }

  private void cincInstruction() {
    counterReg++;
  }

  private void ctsInstruction() {
    push(counterReg);
  }

  private void stcInstruction() {
    counterReg = pop();
  }

  private void termInstruction() {
    terminate();
  }

  private void outnhInstuction() {
    int x = pop();
    System.out.printf("%d\n", x);
    push(x);
  }

  private void mulInstruction() {
    push(pop() * pop());
  }
}

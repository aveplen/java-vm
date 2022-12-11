package markin.stackmachine.vm;

public enum Instruction {
  NOOP,
  ADD,
  SUB,
  AND,
  OR,
  XOR,
  NOT,
  IN,
  OUT,
  LOAD,
  STOR,
  JMP,
  JZ,
  JNZ,
  PUSH,
  DUP,
  SWAP,
  ROLR,
  ROLL,
  DROP,
  COMP,
  CDEC,
  CINC,
  CTS,
  STC,
  TERM,
  OUTNH,
  MUL;

  public static Instruction decode(int opcode) {
    for (Instruction inst : values()) {
      if (inst.ordinal() == opcode) {
        return inst;
      }
    }
    throw new RuntimeException("unknown opcode " + opcode);
  }

  public static int encode(String mnemonics) {
    for (Instruction inst : values()) {
      if (inst.toString().equals(mnemonics)) {
        return inst.ordinal();
      }
    }
    throw new RuntimeException("unknown mnemonics " + mnemonics);
  }

  public static int encode(Instruction instruction) {
    return instruction.ordinal();
  }
}

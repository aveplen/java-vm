package markin.stackmachine.tasks;

import markin.stackmachine.vm.Cpu;

public class VmConvolution implements ConvolutionTask {

  private final Cpu vm;

  public VmConvolution(int[] arr1, int[] arr2) {
    int[] mem = new int[arr1.length + arr2.length];
    System.arraycopy(arr1, 0, mem, 0, arr1.length);
    System.arraycopy(arr2, 0, mem, arr1.length, arr2.length);
    this.vm = new Cpu(
        VmConvolutionContext.getCode(),
        mem,
        arr1.length * arr2.length * 2
    );
  }

  @Override
  public Integer call() {
    vm.run();
    return vm.getStack()[0];
  }
}

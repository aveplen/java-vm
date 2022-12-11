package markin.stackmachine.tasks;

public class Convolution implements ConvolutionTask {

  private final int[] arr1;
  private final int[] arr2;

  public Convolution(int[] arr1, int[] arr2) {
    this.arr1 = arr1;
    this.arr2 = arr2;
  }

  @Override
  public Integer call() {
    int res = 0;
    for (int i = 0; i < arr1.length; i++) {
      res += arr1[i] * arr2[i];
    }
    return res;
  }
}

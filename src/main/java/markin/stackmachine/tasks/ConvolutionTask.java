package markin.stackmachine.tasks;

import java.util.concurrent.Callable;

public interface ConvolutionTask extends Callable<Integer> {
  @Override
  Integer call();
}
package markin.stackmachine.app;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import markin.stackmachine.tasks.ConvolutionTask;
import markin.stackmachine.tasks.VmConvolution;

public class GpuApp {

  private final static int matrixSize = 10;

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    int[][] matr1 = generateMatrix();
    System.out.println(matrixToString(matr1));

    int[][] matr2 = generateMatrix();
    System.out.println(matrixToString(matr2));

    List<ConvolutionTask> convolutions = prepareTasks(matr1, matr2);
    ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    List<Future<Integer>> results = forkJoinPool.invokeAll(convolutions);

    int[][] result = joinResults(results);
    System.out.println(matrixToString(result));
  }

  private static int[][] generateMatrix() {
    int[][] matr = new int[matrixSize][];
    for (int i = 0; i < matrixSize; i++) {
      matr[i] = new int[matrixSize];

      for (int j = 0; j < matrixSize; j++) {
        matr[i][j] = ThreadLocalRandom.current().nextInt() % 10;
      }
    }
    return matr;
  }

  private static String matrixToString(int[][] matrix) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        sb.append(String.format("%d ", matrix[i][j]));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  private static List<ConvolutionTask> prepareTasks(int[][] matr1, int[][] matr2) {
    List<ConvolutionTask> convolutions = new LinkedList<>();

    for (int i = 0; i < matrixSize; i++) {
      int[] arr1 = new int[matrixSize+1];
      arr1[0] = matrixSize;
      System.arraycopy(matr1[i], 0, arr1, 1, matrixSize);


      for (int j = 0; j < matrixSize; j++) {
        int[] arr2 = new int[matrixSize+1];
        arr2[0] = matrixSize;

        for (int k = 0; k < matrixSize; k++) {
          arr2[k+1] = matr2[k][j];
        }

        convolutions.add(new VmConvolution(arr1, arr2));
      }
    }
    return convolutions;
  }

  private static int[][] joinResults(List<Future<Integer>> futures)
      throws ExecutionException, InterruptedException {

    int[][] result = new int[matrixSize][];
    for (int i = 0; i < matrixSize; i++) {
      result[i] = new int[matrixSize];

      for (int j = 0; j < matrixSize; j++) {
        result[i][j] = futures.get(i * matrixSize + j).get();
      }
    }
    return result;
  }
}

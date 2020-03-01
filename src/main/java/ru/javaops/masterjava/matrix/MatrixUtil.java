package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

//todo try
//        final CompletionService<Boolean> completionService = new
//                ExecutorCompletionService<>(executor);

        final int[][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        final int numberOfTask = MainMatrix.THREAD_NUMBER * 10;
        List<Callable<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfTask; i++) {
            int chunk = matrixSize / numberOfTask;
            int begin = chunk * i;
            int end = chunk * i + chunk;
//            completionService.submit(() -> calculate(begin, end, matrixA, matrixBT, matrixSize, matrixC));
            futures.add(() -> calculate(begin, end, matrixA, matrixBT, matrixSize, matrixC));
        }
        executor.invokeAll(futures);
        return matrixC;
    }

    private static Boolean calculate(int begin, int end, int[][] matrixA,
                                     int[][] matrixB, int matrixSize, int[][] matrixC) {
        for (int i = begin; i < end; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[j][k];
                }
                //todo synchronize?
//                synchronized (MatrixUtil.class) {
                matrixC[i][j] = sum;
//                }
            }
        }
        return true;
    }


    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixBT[j][k];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    System.out.println("incorrect value in " + i + " : " + j);
                    return false;
                }
            }
        }
        return true;
    }
}

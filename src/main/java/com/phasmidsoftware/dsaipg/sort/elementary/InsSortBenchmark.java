package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.util.Config;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.io.*;

public class InsSortBenchmark {
    public static void main(String[] args) throws IOException {
        int[] arraySize = {1000, 2000, 4000, 8000, 16000};
        int numRuns = 5;

        System.out.printf("%-15s %-15s %-15s %-15s %-15s%n", "Array Size", "Random", "Ordered", "Partial", "Reversed");

        for (int i : arraySize) {
            long randomTime = benchmarkSort(i, numRuns, "random");
            long orderedTime = benchmarkSort(i, numRuns, "ordered");
            long partialOrderedTime = benchmarkSort(i, numRuns, "partial ordered");
            long reversedTime = benchmarkSort(i, numRuns, "reversed");

            System.out.printf("%-15d %-15d %-15d %-15d %-15d%n", i, randomTime, orderedTime, partialOrderedTime, reversedTime);
        }
    }

    public static long benchmarkSort(int n, int numRuns, String type) {
        long totalTime = 0;
        Random random = new Random();

        for(int t = 0; t < numRuns; t++){
            Integer[] array = new Integer[n];

            switch (type) {
                case "random":
                    for (int i = 0; i < n; i++) {
                        array[i] = random.nextInt(n);
                    }
                    break;
                case "ordered":
                    for (int i = 0; i < n; i++) {
                        array[i] = i;
                    }
                    break;
                case "partial ordered":
                    for (int i = 0; i < n; i++) {
                        array[i] = (i<n/2) ? i : random.nextInt(n);
                    }
                    break;
                case "reversed":
                    for (int i = 0; i < n; i++) {
                        array[i] = n - i;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }

            Config config;
            try{
                config = Config.load(InsertionSortComparator.class);
            } catch (IOException e){
                throw new RuntimeException(e);
            }

            Comparator<Integer> comparator = Comparator.naturalOrder();
            InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(
                    comparator, n, 1, config);

            long startTime = System.nanoTime();
            sorter.sort(array,0,n);
            long endTime = System.nanoTime();
            totalTime += endTime-startTime;
        }
        long avgTime = totalTime / numRuns;
        return avgTime;
    }
}

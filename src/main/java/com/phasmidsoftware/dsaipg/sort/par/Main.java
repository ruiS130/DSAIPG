/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();
        int[] array = new int[10_000_000];

        ArrayList<Long> timeList = new ArrayList<>();

        int cutoffNum =  configuration.getOrDefault("R", 60000);

        java.io.File directory = new java.io.File("./ParOutput");
        if (!directory.exists()) {
            if(directory.mkdirs()) {
                System.out.println("Directory ./ParOutput created.");
            } else {
                System.out.println("Directory ./ParOutput not created.");
                return;
            }
        }

        for (int j = 50; j < 100; j++) {
            ParSort.cutoff = cutoffNum * (j);

            for (int k = 0; k < 3; k++) {
                for(int l = 0; l < array.length; l++) {
                    array[l] = random.nextInt(1_000_000);
                }
                ParSort.sort(array, 0, array.length);
            }

            // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
            long totalTime = 0;
            for (int t = 0; t < 10; t++) {
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10_000_000);
                ParSort.sort(array, 0, array.length);
                long endTime = System.currentTimeMillis();
                totalTime += (endTime - startTime);
            }
            long avgTime = totalTime / 10;
            timeList.add(avgTime);

            System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t 10 times Time: " + avgTime + "ms");
        }

        try {
            FileOutputStream fis = new FileOutputStream("./ParOutput/result.csv");
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            int j = 50;
            for (long i : timeList) {
                double factor = (double) cutoffNum * j / array.length;
                String content = factor + "," + (double) i / 10 + "\n";
                j++;
                bw.write(content);
            }
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        for(int i = 0; i < args.length; i+=2) {
            if(i + 1 >= args.length) {
                throw new IllegalArgumentException("Missing value: " + args[i]);
            }
            processCommand(args[i], args[i+1]);
        }
//        String[] xs = args;
//        while (xs.length > 0)
//            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        try{
            int parsedV =  Integer.parseInt(y);
            if(x.equalsIgnoreCase("-T")) {
                configuration.put("T", parsedV);
                System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(parsedV));
            } else if(x.equalsIgnoreCase("-R")) {
                configuration.put("R", parsedV);
            } else {
                System.err.println("Unknown command: " + x);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + y);
        }
//        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
//        else
//            // TODO sort this out
//            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
//                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
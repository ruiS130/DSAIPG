package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.Benchmark;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;

public class PQBenchmark {
    private static final int N = 4095;
    private static final int numInsert = 16000;
    private static final int numDelete = 4000;

    public static void main(String[] args) {
        Comparator<Integer> comparator = Integer::compareTo;
        Benchmark_Timer<Void> timer = new Benchmark_Timer<>("PQ", x -> testPQBinary(comparator));
        double avgTime = timer.runFromSupplier(() ->  null, 10);
        System.out.print("\nAbove are spilled elements with highest priority.");
        System.out.println("\nAverage time for binary heap: " + avgTime + " ms");
        System.out.println();

        Benchmark_Timer<Void> timerF = new Benchmark_Timer<>("PQ Floyd", x -> testPQBinaryFloyd(comparator));
        double avgTimeF = timerF.runFromSupplier(() -> null, 10);
        System.out.print("\nAbove are spilled elements with highest priority.");
        System.out.println("\nAverage Time for Floyd: " + avgTimeF + " ms");
        System.out.println();

        Benchmark_Timer<Void> timerFourA = new Benchmark_Timer<>("4-ary PQ", x -> testPQFourAry(comparator));
        double avgTimeFourA = timerFourA.runFromSupplier(() -> null, 10);
        System.out.print("\nAbove are spilled elements with highest priority.");
        System.out.println("\nAverage Time for 4-ary heaps: " + avgTimeFourA + " ms");
        System.out.println();

        Benchmark_Timer<Void> timerFourAFloyd = new Benchmark_Timer<>("4-ary PQ Floyd", x -> testPQFourAryFloyd(comparator));
        double avgTimeFourAFloyd = timerFourAFloyd.runFromSupplier(() -> null, 10);
        System.out.print("\nAbove are spilled elements with highest priority.");
        System.out.println("\nAverage Time for 4-ary Floyd: " + avgTimeFourAFloyd + " ms");
    }

    private static void testPQBinary(Comparator<Integer> comparator) {
        BinaryHeapPQ<Integer> pq = new BinaryHeapPQ<>(N, false, comparator);
        Random random = new Random();

        Integer spilledP = null;

        for (int i = 0; i < numInsert; i++) {
            int value =  random.nextInt(100000);
            if(pq.size() < N) {
                pq.insert(value);
            } else {
                if(value > pq.peek()) {
                    int spilled = pq.remove();
                    if(spilledP == null || spilled >  spilledP) {
                        spilledP = spilled;
                    }
                    pq.insert(value);
                } else {
                    if(spilledP == null || value > spilledP) {
                        spilledP = value;
                    }
                }
            }
        }
//        System.out.println(pq);
        for(int i = 0; i < numDelete; i++) {
            pq.remove();
        }
        System.out.print(spilledP + " ");
    }

    private static void testPQBinaryFloyd(Comparator<Integer> comparator) {
        BinaryHeapPQ<Integer> pq = new BinaryHeapPQ<>(N, false, comparator);
        Random random = new Random();

        Integer spilledP = null;

        int limit = Math.min(numInsert, N);
        for(int i = 0; i < limit; i++) {
            pq.insert(random.nextInt(100000));
        }
        pq.buildHeap();

        for (int i = 0; i < limit; i++) {
            int value =  random.nextInt(100000);
            if(value > pq.peek()) {
                int spilled = pq.remove();
                if(spilledP == null || spilled >  spilledP) {
                    spilledP = spilled;
                }
                pq.insert(value);
            } else {
                    if(spilledP == null || value > spilledP) {
                        spilledP = value;
                    }
            }
        }
//        System.out.println(pq);
        for(int i = 0; i < numDelete && !pq.isEmpty(); i++) {
            pq.remove();
        }
        System.out.print(spilledP + " ");
    }

    private static void testPQFourAry(Comparator<Integer> comparator) {
        FourAryHeapPQ<Integer> pq = new FourAryHeapPQ<>(N, comparator, false);
        Random random = new Random();

        Integer spilledP = null;

        for (int i = 0; i < numInsert; i++) {
            int value =  random.nextInt(100000);
            if(pq.size() < N) {
                pq.insert(value);
            } else {
                if(value > pq.peek()) {
                    int spilled = pq.remove();
                    if(spilledP == null || spilled >  spilledP) {
                        spilledP = spilled;
                    }
                    pq.insert(value);
                } else {
                    if(spilledP == null || value > spilledP) {
                        spilledP = value;
                    }
                }
            }
        }
//        System.out.println(pq);
        for(int i = 0; i < numDelete && !pq.isEmpty(); i++) {
            pq.remove();
        }
        System.out.print(spilledP + " ");
    }

    private static void testPQFourAryFloyd(Comparator<Integer> comparator) {
        FourAryHeapPQ<Integer> pq = new FourAryHeapPQ<>(N, comparator, false);
        Random random = new Random();

        Integer spilledP = null;

        int limit = Math.min(numInsert, N);
        for(int i = 0; i < limit; i++) {
            pq.insert(random.nextInt(100000));
        }
        pq.buildHeap();

        for (int i = 0; i < limit; i++) {
            int value =  random.nextInt(100000);
            if(value > pq.peek()) {
                int spilled = pq.remove();
                if(spilledP == null || spilled >  spilledP) {
                    spilledP = spilled;
                }
                pq.insert(value);
            } else {
                if(spilledP == null || value > spilledP) {
                    spilledP = value;
                }
            }
        }
//        System.out.println(pq);
        for(int i = 0; i < numDelete && !pq.isEmpty(); i++) {
            pq.remove();
        }
        System.out.print(spilledP + " ");
    }
}






package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import org.checkerframework.checker.units.qual.K;

import java.util.Comparator;

public class BinrayPQ<K> {

    private K[] heap;
    private int size;
    private final Comparator<K> comparator;
    private final boolean max;

    public BinrayPQ(int capacity, boolean max, Comparator<K> comparator) {
        this.comparator = comparator;
        this.max = max;
        this.heap = (K[]) new Object[capacity+1];
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(K key) {
        if(size >= heap.length - 1) {
            throw new RuntimeException("Heap is full");
        }
        heap[++size] = key;
        swim(size);
    }

    public K remove() {
        if(isEmpty()) {
            throw new RuntimeException("Heap is empty");}
        K top =  heap[1];
        swap(1,size);
        heap[size] = null;
        size--;
        sink(1);
        return top;
    }

    public void swim(int k) {
        while(k>1 && less((k/2), k)) {
            swap(k, (k/2));
            k = k/2;
        }
    }

    public void sink(int k) {
        while(k*2 <=size) {
            int j = k*2;
            if(j<size && less(j, j+1)) {
                j++;
            }
            if(!less(k, j)) {
                break;
            }
            swap(k, j);
            k = j;
        }
    }

    private void swap(int i, int j) {
        K tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private boolean less(int i, int j) {
        int cmp = comparator.compare(heap[i], heap[j]);
        if(max) {
            return cmp < 0;
        } else {
            return cmp > 0;
        }
    }

    public static void main(String[] args) {
        Comparator<Integer> cmp = Integer::compareTo;
        PriorityQueue<Integer> pq = new PriorityQueue<>(1, cmp);
    }
}

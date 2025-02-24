package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BinaryHeapPQ<K> {

    private K[] heap;
    private int size;
    private final Comparator<K> comparator;
    private final boolean max;

    public BinaryHeapPQ(int capacity, boolean max, Comparator<K> comparator) {
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

    public K peek() {
        if(isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }
        return heap[1];
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
        while(k>1 && compareLess((k/2), k)) {
            swap(k,(k/2));
            k = k/2;
        }
    }

    public void sink(int k) {
        while(k*2 <=size) {
            int j = k*2;
            if(j<size && compareLess(j, j+1)) {
                j++;
            }
            if(!compareLess(k, j)){
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

    private boolean compareLess(int i, int j) {
        int compare = comparator.compare(heap[i], heap[j]);
        if(max==true) {
            return compare < 0;
        } else {
            return compare > 0;
        }
    }

    public void buildHeap() {
        for(int i = size / 2; i>=1; i--) {
            sink(i);
        }
    }

    public void snake(int index){

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= size; i++){
            sb.append(heap[i]).append(" ");
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        Comparator<Integer> compare = Integer::compareTo;
//        BinaryHeapPQ<Integer> maxHeap = new BinaryHeapPQ<>(10, true, cmp);
//    }
}

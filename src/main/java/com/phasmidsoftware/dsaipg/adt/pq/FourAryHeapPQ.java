package com.phasmidsoftware.dsaipg.adt.pq;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FourAryHeapPQ<K> {

    private K[] heap;
    private int size;
    private final Comparator<K> comparator;
    private final boolean max;

    public FourAryHeapPQ(int capacity, Comparator<K> comparator, boolean max) {
        this.comparator = comparator;
        this.max = max;
        heap = (K[]) new Object[capacity + 1];
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
        while (k > 1) {
            int parent = (k - 2) / 4 + 1;
            if (compareLess(parent, k)) {
                swap(parent, k);
                k = parent;
            } else {
                break;
            }
        }
    }

    public void sink(int k) {
        while(true) {
            int child1 = 4*(k-1)+2;
            if(child1 > size) {
                break;
            }
            int childBest = child1;
            int childLast = Math.min(child1+3, size);
            for(int i = child1; i < childLast; i++) {
                if(compareLess(childBest, i)) {
                    childBest = i;
                }
            }
            if(!compareLess(k, childBest)) {
                break;
            }
            swap(k, childBest);
            k = childBest;
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
        int parentLast = (size - 2)/4 + 1;
        for(int i = parentLast ; i>= 1; i--) {
            sink(i);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= size; i++){
            sb.append(heap[i]).append(" ");
        }
        return sb.toString();
    }

//        public static void main(String[] args) {
//        Comparator<Integer> compare = Integer::compareTo;
//        FourAryHeapPQ<Integer> heap1 = new FourAryHeapPQ<>(10, compare, true);
//
//        heap1.insert(1);
//        heap1.insert(244);
//        heap1.insert(34);
//        heap1.insert(4);
//
//        System.out.println(heap1.toString());
//        System.out.println(heap1.peek());
//        System.out.println(heap1.remove());
//        System.out.println(heap1.toString());
//    }
}

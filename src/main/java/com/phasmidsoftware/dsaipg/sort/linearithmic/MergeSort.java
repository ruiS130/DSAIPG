/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    @Override
    public X[] sort(X[] array) throws SortException {
        return sort(array, true);
    }

    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        Helper<X> helper1 = helper.clone("MergeSort: insertionSort");
        return new InsertionSort<>(helper1);
    }

    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        insertionSort.getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        boolean noCopy = getHelper().getConfig().getBoolean(MERGESORT, NOCOPY);
        @SuppressWarnings("unchecked")
        X[] aux = noCopy ? getHelper().copyArray(result) : (X[]) new Comparable[result.length];
        mergeSort(result, aux, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    public void sort(X[] a, int from, int to) {
        getHelper().init(a.length);
        insertionSort.getHelper().init(a.length);
//        System.out.println("insurance: " + config.getBoolean(MERGESORT, INSURANCE));
//        System.out.println("nocopy: " + config.getBoolean(MERGESORT, NOCOPY));
        boolean noCopy = getHelper().getConfig().getBoolean(MERGESORT, NOCOPY);
        @SuppressWarnings("unchecked")
        X[] aux = noCopy ? getHelper().copyArray(a) : (X[]) new Comparable[a.length];
        mergeSort(a, aux, from, to);
    }

    private void mergeSort(X[] a, X[] aux, int from, int to) {
        if (to - from <= getHelper().cutoff()) { // XXX check that a cutoff value of 1 effectively stops the cutoff mechanism.
            insertionSort.sort(a, from, to);
            return;
        }
        int mid = from + (to - from) / 2;
        mergeSort(a, aux, from, mid);
        mergeSort(a, aux, mid, to);
//        if(getHelper().getConfig().getBoolean(MERGESORT, INSURANCE) && !getHelper().less(a[mid], a[mid-1])) {
//            return;
//        }
        if(getHelper().getConfig().getBoolean(MERGESORT, INSURANCE)) {
            getHelper().incrementCompares();
            if (!getHelper().less(a[mid], a[mid-1])) {
                return;
            }
        }
        for (int i = from; i < to; i++) {
            getHelper().copy(a[i], aux, i);
        }
        merge(aux, a, from, mid, to);
    }

    // CONSIDER combine with MergeSortBasic, perhaps.
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        int i = from;
        int j = mid;
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                getHelper().copy(getHelper().get(sorted, j), result, k);
                j++;
            } else if (j >= to) {
                getHelper().copy(getHelper().get(sorted, i), result, k);
                i++;
            } else if (getHelper().less(getHelper().get(sorted, j), getHelper().get(sorted, i))) {
                getHelper().incrementFixes(mid - i);
                getHelper().copy(getHelper().get(sorted, j), result, k);
                j++;
            } else {
                getHelper().copy(getHelper().get(sorted, i), result, k);
                i++;
            }
        }
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
        int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
        if (cutoff != CUTOFF_DEFAULT) {
            if (cutoff == 1) stringBuilder.append(" with no cutoff");
            else stringBuilder.append(" with cutoff ").append(cutoff);
        }
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;
    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }

}

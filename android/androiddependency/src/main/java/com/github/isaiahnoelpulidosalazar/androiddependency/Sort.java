package com.github.isaiahnoelpulidosalazar.androiddependency;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A utility class providing a comprehensive collection of sorting algorithm implementations,
 * all operating on {@code int[]} arrays (or {@code double[]} for bucket sort).
 *
 * <p>All methods sort in <b>ascending</b> order. Most methods sort the input array in-place
 * and return it; a few (e.g., {@link #quicksort}, {@link #mergeSort}, {@link #introsort},
 * {@link #timsort}) return a new array.</p>
 *
 * <p>Available algorithms:</p>
 * <ul>
 *   <li>{@link #bubbleSort} — O(n²), simple comparison sort</li>
 *   <li>{@link #cocktailShakerSort} — O(n²), bidirectional bubble sort</li>
 *   <li>{@link #oddEvenSort} — O(n²), parallel-friendly bubble variant</li>
 *   <li>{@link #selectionSort} — O(n²), minimal swaps</li>
 *   <li>{@link #insertionSort} — O(n²), efficient for nearly-sorted data</li>
 *   <li>{@link #shellsort} — O(n log² n), gap-based insertion sort</li>
 *   <li>{@link #quicksort} — O(n log n) average, divide-and-conquer</li>
 *   <li>{@link #mergeSort} — O(n log n), stable divide-and-conquer</li>
 *   <li>{@link #heapsort} — O(n log n), using a priority queue</li>
 *   <li>{@link #introsort} — O(n log n), hybrid quick/heap sort</li>
 *   <li>{@link #timsort} — O(n log n), uses {@link Arrays#sort}</li>
 *   <li>{@link #countingSort} — O(n + k), for non-negative integers with limited range</li>
 *   <li>{@link #bucketSortUniform} — O(n + k), for uniformly distributed doubles in [0, 1)</li>
 *   <li>{@link #pigeonholeSort} — O(n + range), for integers with known min/max</li>
 *   <li>{@link #treeSort} — O(n log n), uses a binary search tree</li>
 *   <li>{@link #patienceSorting} — O(n log n), pile-based merge sort</li>
 *   <li>{@link #bogoSort} — O((n+1)!), random shuffle until sorted (educational only)</li>
 *   <li>{@link #beadSort} — O(S) where S = sum of elements, positive integers only</li>
 * </ul>
 */
public class Sort {

    /**
     * Sorts an array using bubble sort.
     *
     * <p>Repeatedly steps through the list, compares adjacent elements, and swaps them
     * if they are in the wrong order. Time complexity: O(n²).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

    /**
     * Sorts an array using cocktail shaker sort (bidirectional bubble sort).
     *
     * <p>Alternates between forward and backward passes, shrinking the unsorted bounds
     * after each pass. Slightly more efficient than bubble sort for nearly-sorted data.
     * Time complexity: O(n²).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] cocktailShakerSort(int[] arr) {
        boolean swapped = true;
        int start = 0;
        int end = arr.length - 1;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; ++i) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
            end = end - 1;
            for (int i = end - 1; i >= start; i--) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            start = start + 1;
        }
        return arr;
    }

    /**
     * Sorts an array using odd-even sort.
     *
     * <p>Alternates between comparing/swapping odd-indexed and even-indexed adjacent pairs,
     * repeating until no swaps occur. Suitable for parallel implementations.
     * Time complexity: O(n²).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] oddEvenSort(int[] arr) {
        boolean isSorted = false;
        while (!isSorted) {
            isSorted = true;
            for (int i = 1; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    isSorted = false;
                }
            }
            for (int i = 0; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    isSorted = false;
                }
            }
        }
        return arr;
    }

    /**
     * Sorts an array using selection sort.
     *
     * <p>Repeatedly selects the minimum element from the unsorted portion and moves it
     * to the front. Performs at most O(n) swaps regardless of input. Time complexity: O(n²).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] selectionSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIdx] > arr[j]) {
                    minIdx = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIdx];
            arr[minIdx] = temp;
        }
        return arr;
    }

    /**
     * Sorts an array using insertion sort.
     *
     * <p>Builds a sorted sub-array one element at a time by inserting each new element
     * into its correct position. Efficient for small or nearly-sorted arrays.
     * Time complexity: O(n²) worst case, O(n) best case.</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j];
                j -= 1;
            }
            arr[j + 1] = key;
        }
        return arr;
    }

    /**
     * Sorts an array using Shell sort.
     *
     * <p>An optimization of insertion sort that starts by comparing elements far apart
     * and progressively reduces the gap. Time complexity: O(n log² n) with the gap
     * sequence used here (n/2, n/4, …, 1).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] shellsort(int[] arr) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j = i;
                while (j >= gap && arr[j - gap] > temp) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }
                arr[j] = temp;
            }
        }
        return arr;
    }

    /**
     * Sorts an array using quicksort with a middle-element pivot.
     *
     * <p>Partitions the array into elements less than, equal to, and greater than the pivot,
     * then recursively sorts each partition using Java streams. Returns a <b>new</b> sorted array.
     * Time complexity: O(n log n) average, O(n²) worst case.</p>
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] quicksort(int[] arr) {
        if (arr.length <= 1) return arr;
        int pivot = arr[arr.length / 2];
        int[] left = Arrays.stream(arr).filter(x -> x < pivot).toArray();
        int[] middle = Arrays.stream(arr).filter(x -> x == pivot).toArray();
        int[] right = Arrays.stream(arr).filter(x -> x > pivot).toArray();

        int[] result = new int[arr.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(middle, 0, result, left.length, middle.length);
        System.arraycopy(right, 0, result, left.length + middle.length, right.length);
        return result;
    }

    /**
     * Sorts an array using merge sort.
     *
     * <p>Recursively splits the array in half, sorts each half, and merges the results.
     * Stable sort. Returns a <b>new</b> sorted array.
     * Time complexity: O(n log n).</p>
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] mergeSort(int[] arr) {
        if (arr.length <= 1) return arr;
        int mid = arr.length / 2;
        int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
        int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));

        int[] res = new int[arr.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] < right[j]) {
                res[k++] = left[i++];
            } else {
                res[k++] = right[j++];
            }
        }
        while (i < left.length) res[k++] = left[i++];
        while (j < right.length) res[k++] = right[j++];
        return res;
    }

    /**
     * Sorts an array using heap sort via a {@link PriorityQueue}.
     *
     * <p>All elements are inserted into a min-heap and extracted in order.
     * Sorts in-place by writing extracted elements back into the original array.
     * Time complexity: O(n log n).</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] heapsort(int[] arr) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int x : arr) pq.add(x);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = pq.poll();
        }
        return arr;
    }

    /**
     * Sorts an array using introsort (a hybrid of quicksort and heapsort).
     *
     * <p>Uses quicksort with a first-element pivot but falls back to {@link #heapsort}
     * when the recursion depth exceeds {@code 2 * floor(log2(n))} to guarantee O(n log n)
     * worst-case performance. Returns a <b>new</b> sorted array.</p>
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] introsort(int[] arr) {
        int maxDepth = 2 * (int) Math.floor(Math.log(arr.length) / Math.log(2));
        return introSortHelper(arr, maxDepth);
    }

    /**
     * Recursive helper for {@link #introsort(int[])}.
     *
     * @param arr   the sub-array to sort
     * @param depth remaining recursion depth before falling back to heap sort
     * @return a new sorted array
     */
    private static int[] introSortHelper(int[] arr, int depth) {
        if (arr.length <= 1) return arr;
        if (depth == 0) return heapsort(arr);

        int pivot = arr[0];
        int[] left = Arrays.stream(arr).skip(1).filter(x -> x <= pivot).toArray();
        int[] right = Arrays.stream(arr).skip(1).filter(x -> x > pivot).toArray();

        int[] sortedLeft = introSortHelper(left, depth - 1);
        int[] sortedRight = introSortHelper(right, depth - 1);

        int[] res = new int[arr.length];
        System.arraycopy(sortedLeft, 0, res, 0, sortedLeft.length);
        res[sortedLeft.length] = pivot;
        System.arraycopy(sortedRight, 0, res, sortedLeft.length + 1, sortedRight.length);
        return res;
    }

    /**
     * Sorts an array using timsort (via {@link Arrays#sort}).
     *
     * <p>Returns a <b>new</b> sorted array, leaving the original unchanged.
     * Time complexity: O(n log n), stable sort.</p>
     *
     * @param arr the array to sort
     * @return a new sorted copy of the input
     */
    public static int[] timsort(int[] arr) {
        int[] res = arr.clone();
        Arrays.sort(res);
        return res;
    }

    /**
     * Sorts an array of non-negative integers using counting sort.
     *
     * <p>Counts the occurrences of each value up to the maximum, then reconstructs
     * the sorted array. Efficient when the value range is small relative to the input size.
     * Time complexity: O(n + k), where k is the maximum value. Not suitable for large value ranges.</p>
     *
     * @param arr the array of non-negative integers to sort (not modified)
     * @return a new sorted array, or the original if it is {@code null} or empty
     */
    public static int[] countingSort(int[] arr) {
        if (arr == null || arr.length == 0) return arr;
        int maxVal = Arrays.stream(arr).max().getAsInt();
        int[] counts = new int[maxVal + 1];
        for (int x : arr) counts[x]++;

        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                res[idx++] = i;
            }
        }
        return res;
    }

    /**
     * Sorts an array of {@code double} values in [0, 1) using bucket sort.
     *
     * <p>Distributes values into {@code n} equal-width buckets, sorts each bucket
     * individually using {@link Collections#sort}, then concatenates the results.
     * Optimal for uniformly distributed input. Sorts in-place.
     * Time complexity: O(n + k) average.</p>
     *
     * @param arr the array of doubles in [0.0, 1.0) to sort (modified in-place)
     * @return the sorted array
     */
    public static double[] bucketSortUniform(double[] arr) {
        List<List<Double>> buckets = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            buckets.add(new ArrayList<>());
        }
        for (double x : arr) {
            int index = (int) (x * arr.length);
            if (index >= arr.length) index = arr.length - 1;
            buckets.get(index).add(x);
        }
        int idx = 0;
        for (List<Double> b : buckets) {
            Collections.sort(b);
            for (double x : b) {
                arr[idx++] = x;
            }
        }
        return arr;
    }

    /**
     * Sorts an array of integers using pigeonhole sort.
     *
     * <p>Allocates a "hole" for each integer value between the minimum and maximum,
     * counts occurrences, and reconstructs the sorted array. Similar to counting sort
     * but works with arbitrary integer ranges (including negatives).
     * Time complexity: O(n + range), where range = max − min + 1.</p>
     *
     * @param arr the array to sort; may contain negative values (not modified)
     * @return a new sorted array, or the original if empty
     */
    public static int[] pigeonholeSort(int[] arr) {
        if (arr.length == 0) return arr;
        int minVal = Arrays.stream(arr).min().getAsInt();
        int maxVal = Arrays.stream(arr).max().getAsInt();
        int size = maxVal - minVal + 1;
        int[] holes = new int[size];

        for (int x : arr) holes[x - minVal]++;

        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < holes[i]; j++) {
                res[idx++] = i + minVal;
            }
        }
        return res;
    }

    /**
     * Internal binary search tree node used by {@link #treeSort(int[])}.
     */
    static class Node {
        int v;
        Node l, r;
        Node(int v) { this.v = v; }
    }

    /**
     * Sorts an array using tree sort.
     *
     * <p>Inserts all elements into a binary search tree (BST), then performs an
     * in-order traversal to collect the elements in sorted order. Does not handle
     * duplicate elements in any special way — duplicates go to the right subtree.
     * Time complexity: O(n log n) average, O(n²) for sorted input (degenerate BST).</p>
     *
     * @param arr the array to sort
     * @return a new sorted array, or the original if empty
     */
    public static int[] treeSort(int[] arr) {
        if (arr.length == 0) return arr;
        Node root = null;
        for (int x : arr) {
            root = insert(root, x);
        }
        List<Integer> res = new ArrayList<>();
        traverse(root, res);
        return res.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Inserts a value into the BST rooted at {@code root}.
     *
     * @param root the current root node; may be {@code null}
     * @param val  the value to insert
     * @return the root of the updated BST
     */
    private static Node insert(Node root, int val) {
        if (root == null) return new Node(val);
        if (val < root.v) root.l = insert(root.l, val);
        else root.r = insert(root.r, val);
        return root;
    }

    /**
     * Performs an in-order traversal of the BST rooted at {@code root},
     * appending values to {@code res} in ascending order.
     *
     * @param root the current node; may be {@code null}
     * @param res  the list to append values into
     */
    private static void traverse(Node root, List<Integer> res) {
        if (root != null) {
            traverse(root.l, res);
            res.add(root.v);
            traverse(root.r, res);
        }
    }

    /**
     * Sorts an array using patience sorting.
     *
     * <p>Elements are distributed into "piles" (similar to solitaire), and then merged
     * using a min-heap priority queue. The number of piles equals the length of the
     * longest increasing subsequence.
     * Time complexity: O(n log n).</p>
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] patienceSorting(int[] arr) {
        List<List<Integer>> piles = new ArrayList<>();
        for (int x : arr) {
            int left = 0, right = piles.size();
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (piles.get(mid).get(piles.get(mid).size() - 1) >= x) right = mid;
                else left = mid + 1;
            }
            if (left < piles.size()) {
                piles.get(left).add(x);
            } else {
                List<Integer> newPile = new ArrayList<>();
                newPile.add(x);
                piles.add(newPile);
            }
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        for (int i = 0; i < piles.size(); i++) {
            pq.add(new int[]{piles.get(i).get(piles.get(i).size() - 1), i, piles.get(i).size() - 1});
        }

        int[] res = new int[arr.length];
        int idx = 0;
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            res[idx++] = curr[0];
            int pileIdx = curr[1];
            int itemIdx = curr[2] - 1;
            if (itemIdx >= 0) {
                pq.add(new int[]{piles.get(pileIdx).get(itemIdx), pileIdx, itemIdx});
            }
        }
        return res;
    }

    /**
     * Sorts an array using bogo sort (random shuffle sort).
     *
     * <p><b>Warning:</b> This algorithm has expected time complexity O((n+1)!) and is intended
     * for educational or humorous purposes only. It randomly shuffles the array until it
     * happens to be sorted. Never use in production code.</p>
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array (eventually)
     */
    public static int[] bogoSort(int[] arr) {
        Random rand = new Random();
        while (!isSorted(arr)) {
            for (int i = arr.length - 1; i > 0; i--) {
                int index = rand.nextInt(i + 1);
                int a = arr[index];
                arr[index] = arr[i];
                arr[i] = a;
            }
        }
        return arr;
    }

    /**
     * Checks whether the given array is sorted in ascending order.
     *
     * @param arr the array to check
     * @return {@code true} if sorted; {@code false} otherwise
     */
    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }

    /**
     * Sorts an array of non-negative integers using bead sort (gravity sort).
     *
     * <p>Simulates rows of beads falling under gravity on parallel rods. Only works for
     * non-negative integers. Time complexity: O(S) where S is the sum of all elements,
     * or O(n * max) in the matrix-based simulation used here.</p>
     *
     * @param arr the array of non-negative integers to sort
     * @return a new sorted array
     * @throws IllegalArgumentException if any element is negative
     */
    public static int[] beadSort(int[] arr) {
        for (int x : arr) {
            if (x < 0) throw new IllegalArgumentException("Bead sort for positive integers only");
        }
        if (arr.length == 0) return arr;
        int maxVal = Arrays.stream(arr).max().getAsInt();
        int[][] beads = new int[arr.length][maxVal];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i]; j++) {
                beads[i][j] = 1;
            }
        }

        for (int j = 0; j < maxVal; j++) {
            int sumBeads = 0;
            for (int i = 0; i < arr.length; i++) {
                sumBeads += beads[i][j];
            }
            for (int i = 0; i < arr.length; i++) {
                beads[i][j] = (i >= arr.length - sumBeads) ? 1 : 0;
            }
        }

        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < maxVal; j++) rowSum += beads[i][j];
            res[i] = rowSum;
        }
        return res;
    }
}
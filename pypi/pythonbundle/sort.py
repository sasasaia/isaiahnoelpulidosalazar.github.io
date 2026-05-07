from node import Node
import math
import random

def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr

def cocktail_shaker_sort(arr):
    n = len(arr)
    swapped = True
    start, end = 0, n - 1
    while swapped:
        swapped = False
        for i in range(start, end):
            if arr[i] > arr[i + 1]:
                arr[i], arr[i + 1] = arr[i + 1], arr[i]
                swapped = True
        if not swapped: break
        end -= 1
        for i in range(end - 1, start - 1, -1):
            if arr[i] > arr[i + 1]:
                arr[i], arr[i + 1] = arr[i + 1], arr[i]
                swapped = True
        start += 1
    return arr

def odd_even_sort(arr):
    n = len(arr)
    sorted_bool = False
    while not sorted_bool:
        sorted_bool = True
        for i in range(1, n - 1, 2):
            if arr[i] > arr[i + 1]:
                arr[i], arr[i + 1] = arr[i + 1], arr[i]
                sorted_bool = False
        for i in range(0, n - 1, 2):
            if arr[i] > arr[i + 1]:
                arr[i], arr[i + 1] = arr[i + 1], arr[i]
                sorted_bool = False
    return arr

def selection_sort(arr):
    for i in range(len(arr)):
        min_idx = i
        for j in range(i + 1, len(arr)):
            if arr[min_idx] > arr[j]: min_idx = j
        arr[i], arr[min_idx] = arr[min_idx], arr[i]
    return arr

def insertion_sort(arr):
    for i in range(1, len(arr)):
        key = arr[i]
        j = i - 1
        while j >= 0 and key < arr[j]:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key
    return arr

def shellsort(arr):
    n = len(arr)
    gap = n // 2
    while gap > 0:
        for i in range(gap, n):
            temp = arr[i]
            j = i
            while j >= gap and arr[j - gap] > temp:
                arr[j] = arr[j - gap]
                j -= gap
            arr[j] = temp
        gap //= 2
    return arr

def quicksort(arr):
    if len(arr) <= 1: return arr
    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]
    return quicksort(left) + middle + quicksort(right)

def merge_sort(arr):
    if len(arr) <= 1: return arr
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])

    res, i, j = [], 0, 0
    while i < len(left) and j < len(right):
        if left[i] < right[j]:
            res.append(left[i])
            i += 1
        else:
            res.append(right[j])
            j += 1
    return res + left[i:] + right[j:]

def heapsort(arr):
    import heapq
    heapq.heapify(arr)
    return [heapq.heappop(arr) for _ in range(len(arr))]

def introsort(arr):
    max_depth = 2 * math.floor(math.log2(len(arr)))
    def sort_helper(arr, depth):
        if len(arr) <= 1: return arr
        if depth == 0: return heapsort(arr)
        pivot = arr[0]
        left = [x for x in arr[1:] if x <= pivot]
        right = [x for x in arr[1:] if x > pivot]
        return sort_helper(left, depth - 1) + [pivot] + sort_helper(right, depth - 1)
    return sort_helper(arr, max_depth)

def timsort(arr):
    return sorted(arr)

def counting_sort(arr):
    if not arr: return arr
    max_val = max(arr)
    counts = [0] * (max_val + 1)
    for x in arr: counts[x] += 1
    res = []
    for i, count in enumerate(counts):
        res.extend([i] * count)
    return res

def bucket_sort_uniform(arr):
    buckets = [[] for _ in range(len(arr))]
    for x in arr:
        index = int(x * len(arr))
        buckets[index].append(x)
    for b in buckets: b.sort()
    return [x for b in buckets for x in b]

def pigeonhole_sort(a):
    min_val, max_val = min(a), max(a)
    size = max_val - min_val + 1
    holes = [0] * size
    for x in a: holes[x - min_val] += 1
    res = []
    for i in range(size):
        res.extend([i + min_val] * holes[i])
    return res

def tree_sort(arr):
    if not arr: return []
    def insert(root, val):
        if not root: return Node(val)
        if val < root.v: root.l = insert(root.l, val)
        else: root.r = insert(root.r, val)
        return root
    def traverse(root, res):
        if root:
            traverse(root.l, res)
            res.append(root.v)
            traverse(root.r, res)
    root = None
    for x in arr: root = insert(root, x)
    res = []
    traverse(root, res)
    return res

def patience_sorting(arr):
    import bisect
    piles = []
    for x in arr:
        idx = bisect.bisect_left([p[-1] for p in piles], x)
        if idx < len(piles): piles[idx].append(x)
        else: piles.append([x])
    import heapq
    return list(heapq.merge(*[reversed(p) for p in piles]))

def bogosort(arr):
    while not all(arr[i] <= arr[i+1] for i in range(len(arr)-1)):
        random.shuffle(arr)
    return arr

def bead_sort(arr):
    if any(x < 0 for x in arr): raise ValueError("Bead sort for positive integers only")
    max_val = max(arr)
    beads = [[0] * max_val for _ in range(len(arr))]
    for i, x in enumerate(arr):
        for j in range(x): beads[i][j] = 1
    for j in range(max_val):
        sum_beads = sum(beads[i][j] for i in range(len(arr)))
        for i in range(len(arr)):
            beads[i][j] = 1 if i >= len(arr) - sum_beads else 0
    return [sum(row) for row in beads]
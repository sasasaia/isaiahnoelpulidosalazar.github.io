package com.github.isaiahnoelpulidosalazar.androiddependency;

/**
 * A generic, dynamically resizable storage container backed by a plain array.
 *
 * <p>{@code Memory} provides basic collection-like operations (add, remove, get, contains)
 * without relying on Java's standard {@link java.util.List} implementations. Each mutation
 * copies the underlying array, so this class is best suited for small data sets.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Memory<String> memory = new Memory<>();
 * memory.add("hello");
 * memory.add("world");
 * String first = memory.get(0);    // "hello"
 * memory.remove("hello");
 * int size = memory.count();       // 1
 * }</pre>
 *
 * @param <GenericType> the type of elements stored in this container
 */
public class Memory<GenericType> {
    GenericType[] storage;

    /**
     * Constructs an empty {@code Memory} container with zero initial capacity.
     */
    public Memory(){
        storage = (GenericType[]) new Object[0];
    }

    /**
     * Appends an element to the end of the container.
     *
     * <p>The internal array is copied and grown by one element on each call.</p>
     *
     * @param genericObject the element to add; may be {@code null} depending on the type parameter
     */
    public void add(GenericType genericObject){
        GenericType[] temp = (GenericType[]) new Object[storage.length];
        for (int a = 0; a < storage.length; a++){
            temp[a] = storage[a];
        }
        storage = (GenericType[]) new Object[storage.length + 1];
        for (int a = 0; a < temp.length; a++){
            storage[a] = temp[a];
        }
        storage[storage.length - 1] = genericObject;
    }

    /**
     * Checks whether the container holds an element equal to the given object.
     *
     * <p>Equality is determined by {@link Object#equals(Object)}.</p>
     *
     * @param genericObject the element to search for
     * @return {@code true} if a matching element is found; {@code false} otherwise
     */
    public boolean contains(GenericType genericObject){
        for (int a = 0; a < storage.length; a++){
            if (storage[a].equals(genericObject)){
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the first occurrence of the given element from the container.
     *
     * <p>If the element appears more than once, only the first match is removed.
     * If the element is not found, the container is not modified.</p>
     *
     * <p>The internal array is copied and shrunk by one element after removal.</p>
     *
     * @param genericObject the element to remove
     */
    public void remove(GenericType genericObject){
        GenericType[] temp = (GenericType[]) new Object[storage.length - 1];
        int b = 0;
        for (int a = 0; a < storage.length; a++){
            if (!storage[a].equals(genericObject)){
                if (b < temp.length){
                    temp[b] = storage[a];
                    b++;
                }
            }
        }
        storage = (GenericType[]) new Object[temp.length];
        for (int a = 0; a < temp.length; a++) {
            storage[a] = temp[a];
        }
    }

    /**
     * Removes the element at the specified index from the container.
     *
     * <p>All elements after the removed index are shifted one position to the left.
     * The internal array is copied and shrunk by one element.</p>
     *
     * @param index the zero-based index of the element to remove
     * @throws ArrayIndexOutOfBoundsException if {@code index} is out of range
     */
    public void removeAt(int index){
        GenericType[] temp = (GenericType[]) new Object[storage.length - 1];
        int b = 0;
        for (int a = 0; a < storage.length; a++){
            if (a != index){
                temp[b] = storage[a];
                b++;
            }
        }
        storage = (GenericType[]) new Object[temp.length];
        for (int a = 0; a < temp.length; a++) {
            storage[a] = temp[a];
        }
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index the zero-based index of the element to retrieve
     * @return the element at position {@code index}
     * @throws ArrayIndexOutOfBoundsException if {@code index} is out of range
     */
    public GenericType get(int index){
        return storage[index];
    }

    /**
     * Returns the number of elements currently stored in the container.
     *
     * @return the element count; {@code 0} if the container is empty
     */
    public int count(){
        return storage.length;
    }

    /**
     * Removes all elements from the container, resetting it to an empty state.
     */
    public void clear(){
        storage = (GenericType[]) new Object[0];
    }
}
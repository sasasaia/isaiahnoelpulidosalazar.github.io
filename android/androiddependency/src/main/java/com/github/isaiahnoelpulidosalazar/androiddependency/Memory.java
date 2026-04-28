package com.github.isaiahnoelpulidosalazar.androiddependency;

@SuppressWarnings("unchecked")
public class Memory<GenericType> {
    GenericType[] storage;

    public Memory(){
        storage = (GenericType[]) new Object[0];
    }

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

    public boolean contains(GenericType genericObject){
        for (int a = 0; a < storage.length; a++){
            if (storage[a].equals(genericObject)){
                return true;
            }
        }
        return false;
    }

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

    public GenericType get(int index){
        return storage[index];
    }

    public int count(){
        return storage.length;
    }

    public void clear(){
        storage = (GenericType[]) new Object[0];
    }
}
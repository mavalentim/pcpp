package main.java.exercises01;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyClass {
    private Object aiai = new Object(); // no longer used
    private ReadWriteLock lock = new ReentrantReadWriteLock(); // had to use the reentrant lock to divide it into
                                                               // readers and writers
    private int sharedValue = 42;

    // this method of thisclass will work as a reader
    public int getSharedValue() {
        lock.readLock().lock();
        try {
            // Read data from the shared resource
            return this.sharedValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    // this method of thisclass will work as a writer
    public void setSharedValue(int sharedValue) {
        lock.writeLock().lock();
        this.sharedValue = sharedValue;
        lock.writeLock().unlock();
    }

    public static void main(String[] args) {
        MyClass myInstance = new MyClass();

        Thread reader = new Thread() {
            public void run() {
                System.out.println("The shared value is: " + myInstance.getSharedValue());
            }
        };

        Thread reader2 = new Thread() {
            public void run() {
                System.out.println("The shared value is: " + myInstance.getSharedValue());
            }
        };

        // and a thread that will overqwite the sharedValue
        Thread writer = new Thread() {
            public void run() {
                myInstance.setSharedValue(89);
                System.out.println("The shared value is: " + myInstance.getSharedValue());
            }
        };

        reader.start();
        reader2.start();
        writer.start();
        try {
            reader.join();
            reader2.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

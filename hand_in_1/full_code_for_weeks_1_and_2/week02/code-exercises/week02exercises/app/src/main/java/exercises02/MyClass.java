package main.java.exercises01;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyClass {
    // Firsts: create a java intrinsic lock and a condition
    private Object lock = new Object(); // no longer used
    private Condition writerActive = new ReentrantLock().newCondition();
    private Condition noWriterActive = new ReentrantLock().newCondition();

    // Second: create a boolean to check the condition
    private boolean writer = null;

    private int sharedValue = 42;

    // READER METHOD
    public int getSharedValue() {
        synchronized (lock) {
            if (writerActive) {
                System.out.println("Writer active! We wait");
                noWriterActive.await();
            }

            return this.sharedValue;

        }

    }

    // WRITER METHOD
    public void setSharedValue(int sharedValue) {
        // Theres a writer active
        synchronized (lock) {
            writerActive.signalAll();
            this.sharedValue = sharedValue;
            noWriterActive.signalAll();
        }

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

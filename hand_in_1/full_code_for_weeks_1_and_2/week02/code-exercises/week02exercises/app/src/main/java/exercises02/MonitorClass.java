package main.java.exercises02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MonitorClass {

    // Firsts: create a java intrinsic lock and a condition
    private Object lock = new Object(); // no longer used

    // this will be given as a signal by the writers when they done

    // Second: the booleans help check the conditions
    private boolean writerActive = false;
    private int readersActive = 0;
    // private int readerAcquired = 0;
    // private int readerReleased = 0;

    // Third is the main shared value
    private int sharedValue = 42;

    // READER METHOD LOCKING MECHANISM
    public void readLock() {
        synchronized (lock) { // this lock prevents another thread from starting simultaneously something
                              // before this
            while (writerActive) {
                System.out.println("Writer active! We wait");
                this.wait();
            }
            readersActive = readersActive + 1;
            /*
             * readerAcquired++;
             */

        }

    }

    // READER METHOD unLOCKING MECHANISM
    // What are the conditions for unlocking?
    public void readUnLock() {
        synchronized (lock) {
            // readerReleased++;
            readersActive = readersActive - 1;
            if (readersActive == 0) {
                this.notifyAll();
            }

        }

    }

    // WRITER METHOD
    public void writeLock() {
        // Theres a writer active
        synchronized (lock) {
            while (writerActive) { // while another writer is active or there are any readers
                this.wait(); // wait
            }

            writerActive = true;

            while (readersActive > 0) {
                this.wait();
            }
        }

    }

    // write a method like the above but for unlocking
    public void writeUnlock() {
        synchronized (lock) {
            writerActive = false;
            this.notifyAll();
        }
    }

    // method to get the shared value
    public int getSharedValue() {
        return this.sharedValue;
    }

    public void setSharedValue(int newval) {
        this.sharedValue = newval;
    }

    public static void main(String[] args) {
        MonitorClass myInstance = new MonitorClass();

        Thread reader = new Thread() {
            public void run() {
                myInstance.readLock();
                System.out.println("The shared value is: " + myInstance.getSharedValue());
                myInstance.readUnLock();
            }
        };

        Thread reader2 = new Thread() {
            public void run() {
                myInstance.readLock();
                System.out.println("The shared value is: " + myInstance.getSharedValue());
                myInstance.readUnLock();
            }
        };

        // and a thread that will overqwite the sharedValue
        Thread writer = new Thread() {
            public void run() {
                myInstance.writeLock();
                myInstance.setSharedValue(89);
                System.out.println("The shared value is: " + myInstance.getSharedValue());
                myInstance.writeUnlock();
            }
        };

        Thread writer2 = new Thread() {
            public void run() {
                myInstance.writeLock();
                myInstance.setSharedValue(89);
                System.out.println("The shared value is: " + myInstance.getSharedValue());
                myInstance.writeUnlock();
            }
        };

        reader.start();
        reader2.start();
        writer.start();
        writer2.start();
        try {
            reader.join();
            reader2.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

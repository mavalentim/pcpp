package exercises03;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class myBoundedBuffer<T> implements BoundedBufferInteface<T> {
    // fields: the size and the buffer
    private final int bufferSize;
    private ArrayList<T> buffer; // this is where things are put and taken out of
    // AND THE SEMAPHOREs
    private Semaphore semaphor_bi;
    private Semaphore semaphor_mutex;

    // constructor
    myBoundedBuffer(int size) {
        bufferSize = size;
        buffer = new ArrayList<T>();
        semaphor_mutex = new Semaphore(1);
        semaphor_bi = new Semaphore(0);
    }

    @Override
    public T take() throws Exception {
        semaphor_mutex.acquire();
        semaphor_bi.release();
        if (buffer.size() == 0) {
            throw new Exception("Buffer is empty");
        } else {
            buffer.remove(0); // FIFO QUEUE

        }

        // but only releases the bi semaphore: the other one is released when
        semaphor_mutex.release();
        // i had to return something afterwards otherwise it wouldnt compile
        return null;

    }

    @Override
    public void insert(T elem) throws Exception {
        // first acquire the mutex
        semaphor_mutex.acquire();

        if (buffer.size() < bufferSize) { // Only inserts if the capacity allows it
            buffer.add(elem);
            semaphor_bi.acquire();
        } else {
            throw new Exception("Buffer is SO full");
        }

        // releases the mutex
        semaphor_mutex.release();
    }

    public void printThis() {
        for (T iterable_element : buffer) {
            System.out.println(iterable_element);
        }
    }

    public static void main(String[] args) throws Exception {
        myBoundedBuffer<Integer> buffer1 = new myBoundedBuffer<>(2);

        // Create the threads
        Thread producer_of_even_numbers = new Thread() {
            public void run() {
                try {
                    buffer1.insert(2);
                    buffer1.insert(4);
                    buffer1.insert(10);
                    buffer1.insert(8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread consumer1 = new Thread() {
            public void run() {
                try {
                    buffer1.take();
                    buffer1.take();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        // Before starting the threads, Ill create a semaphor and only allow one of the
        // threads to run at a time

        producer_of_even_numbers.start();
        consumer1.start();
        try {
            producer_of_even_numbers.join();
            consumer1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    } // end of MAIN

}

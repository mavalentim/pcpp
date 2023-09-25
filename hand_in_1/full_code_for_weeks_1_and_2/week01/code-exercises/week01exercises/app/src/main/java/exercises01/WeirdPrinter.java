package main.java.exercises01;

import java.util.concurrent.locks.ReentrantLock;

public class WeirdPrinter {

    private static ReentrantLock lock = new ReentrantLock();

    public void run() {
        // loop 10 times
        for (int i = 0; i < 10; i++) {
            print();
        }
    }

    public void print() {
        lock.lock();
        System.out.print("-");
        try {
            Thread.sleep(50);
        } catch (InterruptedException exn) {
        }
        System.out.print("|");
        lock.unlock();
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            WeirdPrinter p = new WeirdPrinter();
            p.run();
        });

        Thread t2 = new Thread(() -> {
            WeirdPrinter p2 = new WeirdPrinter();
            p2.run();
        });
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }

    }
}

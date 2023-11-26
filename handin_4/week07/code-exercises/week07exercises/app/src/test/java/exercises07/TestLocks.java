package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    // This is the test for multiple writers at the same time. It keeps failing.
    @RepeatedTest(200)
    @Test
    void TestingWriters() {
        // im gonna create like a shared value and two writer threads
        Histogram1 testHist = new Histogram1(3);
        ReadWriteCASLock myLock = new ReadWriteCASLock();

        Thread t1 = new Thread(() -> {
            if (myLock.writerTryLock()) {
                try {
                    myLock.writerTryLock();
                    testHist.increment(1);
                    myLock.writerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed");
                    e.printStackTrace();
                }
            }

        });

        Thread t2 = new Thread(() -> {
            if (myLock.writerTryLock()) {
                try {
                    testHist.increment(2);
                    myLock.writerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed2");
                    e.printStackTrace();
                }

            }

        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {

        } finally {
            assertEquals(1, testHist.getMyset());
        }

    }

    // Seq in a single thread: the reader cannot access the lock when the writer has
    // it

    @Test
    void TestingWriters22() {
        // im gonna create like a shared value and two writer threads
        Histogram1 testHist = new Histogram1(3);
        ReadWriteCASLock myLock = new ReadWriteCASLock();

        Thread t1 = new Thread(() -> {
            if (myLock.writerTryLock()) {

                if (myLock.writerTryLock()) {
                    try {
                        testHist.increment(2);
                        myLock.writerUnlock();
                    } catch (Exception e) {
                        System.out.println("someone missed2");
                        e.printStackTrace();
                    }

                }
                try {
                    myLock.writerTryLock();
                    testHist.increment(1);
                    myLock.writerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed");
                    e.printStackTrace();
                }
            }

        });

        t1.start();

        try {
            t1.join();
        } catch (Exception e) {

        } finally {
            assertTrue(testHist.getMyset().contains("1"));
        }

    }

    @Test
    void TestingReadersAndWriters22() {

        final CountDownLatch startLatch = new CountDownLatch(1);
        // im gonna create like a shared value and two writer threads
        Histogram1 testHist = new Histogram1(3);
        ReadWriteCASLock myLock = new ReadWriteCASLock();

        Thread t1 = new Thread(() -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (myLock.writerTryLock()) {
                try {
                    myLock.writerTryLock();
                    testHist.increment(1);
                    myLock.writerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed");
                    e.printStackTrace();
                }
            }

        });

        Thread t2 = new Thread(() -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (myLock.readerTryLock()) {
                try {
                    testHist.increment(2);
                    myLock.readerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed2");
                    e.printStackTrace();
                }

            }

        });

        Thread t3 = new Thread(() -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (myLock.readerTryLock()) {
                try {
                    testHist.increment(3);
                    myLock.readerUnlock();
                } catch (Exception e) {
                    System.out.println("someone missed2");
                    e.printStackTrace();
                }

            }

        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {

        } finally {
            assertTrue((testHist.getMyset().contains("2") && testHist.getMyset().contains("3")
                    && testHist.getMyset().size() == 2)
                    || testHist.getMyset().contains("3") && testHist.getMyset().size() == 1
                    || testHist.getMyset().contains("2") && testHist.getMyset().size() == 1
                    || testHist.getMyset().contains("1") && testHist.getMyset().size() == 1);
        }

    }

    // I got told by CHATGPT that the one above doesnt directly test the
    // specificities of the
    @Test
    void TestWritersOptionCGPT() {
        final ReadWriteCASLock lock = new ReadWriteCASLock();
        final AtomicBoolean testResult = new AtomicBoolean(true);
        final int numThreads = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // Wait until all threads are ready to start

                    if (lock.writerTryLock()) {
                        // Ensure that only one thread can acquire the lock
                        for (int j = 0; j < 1000000; j++) {
                            // Simulate some work
                        }
                        lock.writerUnlock();
                    } else {
                        testResult.set(false); // Test failed if more than one thread acquired the lock
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown(); // Start all threads simultaneously

        try {
            finishLatch.await(); // Wait for all threads to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (testResult.get()) {
            System.out.println("Non-blocking mutual exclusive lock test passed!");
        } else {
            System.out.println("Non-blocking mutual exclusive lock test failed!");
        }
    }

    // And finally I need a test to test wheather the readers and writers phase each
    // other
    // First I will check if they respect each other = 1 reader and 1 writer

}
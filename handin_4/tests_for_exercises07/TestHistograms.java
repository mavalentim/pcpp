package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports
    Histogram1 hist1;
    MyHist myhist;

    /*
     * @BeforeAll
     * void setup() {
     * hist1 = new Histogram1(25);
     * myhist = new MyHist(25);
     * }
     */

    @Test
    void CasHistogramTest() {
        MyHist myhist = new MyHist(25);
        Histogram1 hist1 = new Histogram1(25);

        // Start the threads
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                int value = Histogram1.countFactors(i);
                myhist.increment(value);

            }

        });

        Thread t2 = new Thread(() -> {

            for (int i = 1000000; i < 2000000; i++) {
                int value = Histogram1.countFactors(i);
                myhist.increment(value);
            }

        });

        Thread t3 = new Thread(() -> {

            for (int i = 2000000; i < 3000000; i++) {
                int value = Histogram1.countFactors(i);
                myhist.increment(value);
            }
        });

        Thread t4 = new Thread(() -> {

            for (int i = 3000000; i < 4000000; i++) {
                int value = Histogram1.countFactors(i);
                myhist.increment(value);
            }
        });

        Thread t5 = new Thread(() -> {
            for (int i = 0; i < 4000000; i++) {
                int value = Histogram1.countFactors(i);
                hist1.increment(value);
            }

        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            for (int i = 0; i < 25; i++) {
                assertTrue(hist1.getCount(i) == myhist.getCount(i));
            }

        }

        // assertion part

    }

}

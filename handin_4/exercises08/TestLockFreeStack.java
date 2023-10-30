// raup@itu.dk * 2023-10-20 
package exercises08;

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
import java.util.ArrayList;
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLockFreeStack {
    public static void main(String[] args) {
        new TestLockFreeStack();
    }

    private LockFreeStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new LockFreeStack<>();
    }

    // TODO: 8.2.2 - Test push
    @Test
    public void TestPush() {
        int n = 100; // no of elements to push
        AtomicInteger sum = new AtomicInteger(0); // keeping track of sum
        ExecutorService executor = Executors.newFixedThreadPool(n);
        CountDownLatch latch = new CountDownLatch(n);

        for (int i = 1; i <= n; i++) {
            int xi = i;
            executor.submit(() -> {
                stack.push(xi);
                sum.addAndGet(xi);
                latch.countDown();
            });
        }

        // wait for threads to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        int totalSum = sum.intValue();
        assertEquals(totalSum, calculateStackSum(stack), "Sum should be " + totalSum);
    }

    // Helper method to calculate the sum of elements in the stack
    private int calculateStackSum(LockFreeStack<Integer> stack) {
        int sum = 0;
        while (true) {
            Integer value = stack.pop();
            if (value == null) {
                break;
            }
            sum += value;
        }
        return sum;
    }

    // TODO: 8.2.3 - Test pop

    @Test
    public void TestPop() {
        int n = 100; // no of elements
        AtomicInteger originalSum = new AtomicInteger(0); // keeping track of the sum of original elements
        AtomicInteger poppedSum = new AtomicInteger(0); // keeping track of the sum of popped elements
        ExecutorService executor = Executors.newFixedThreadPool(n);

        // Push elements onto the stack
        for (int i = 1; i <= n; i++) {
            int xi = i;
            originalSum.addAndGet(xi);
            stack.push(xi);
        }

        CountDownLatch latch = new CountDownLatch(n);
        for (int i = 1; i <= n; i++) {
            executor.submit(() -> {
                Integer yi = stack.pop();
                if (yi != null) {
                    poppedSum.addAndGet(yi);
                }
                latch.countDown();
            });
        }

        // Wait on threads to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        int originalTotalSum = originalSum.intValue();
        int poppedTotalSum = poppedSum.intValue();
        assertEquals(originalTotalSum, poppedTotalSum,
                "Sum of popped elements should equal the sum of original elements.");
    }
}

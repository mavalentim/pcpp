package exercises04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
// TODO: Very likely you need to expand the list of imports
import org.junit.jupiter.api.Test;

public class ConcurrentSetTest {

  // Variable with set under test
  private ConcurrentIntegerSet set;
  private ConcurrentIntegerSetSync goodSet;
  private SemaphoreImp semaphore;

  // TODO: Very likely you should add more variables here

  // Uncomment the appropriate line below to choose the class to
  // test
  // Remember that @BeforeEach is executed before each test
  @BeforeEach
  public void initialize() {
    // initialize set
    set = new ConcurrentIntegerSetBuggy();
    goodSet = new ConcurrentIntegerSetSync();
    // set = new ConcurrentIntegerSetLibrary();
    semaphore = new SemaphoreImp(2); // initializing the semaphore with capacity 2

  }

  // FIRST TEST: ADD METHOD
  // The interleaving is one of the set.add, which is not atomic, has one of its
  // sequence of commands that check the given element in the set before while the
  // other set is also checking if that given element is in the set.
  @RepeatedTest(100)
  @Test
  void testAddMethod_1() {
    Thread t1 = new Thread(() -> {
      set.add(1);// this first thread adds 1
      set.add(2);// this first thread adds 2

    });

    Thread t2 = new Thread(() -> {
      set.add(2);// this second thread adds 2
      set.add(4);// this second thread adds 4
    });

    t1.start();
    t2.start();
    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      System.out.println("Interrupted Exception");
    }

    assertTrue(set.size() == 3); // Example assertion
  }

  // SECOND TEST: REMOVE METHOD
  @RepeatedTest(100)
  @Test
  void testRemoveMethod() {
    // so I create some threads that remove elements and then a check if the set has
    // the expected number of elements

    // we already have a data sharing set, so we can use it
    for (int i = 0; i < 10; i++) {
      set.add(i);
    }

    Thread t1 = new Thread(() -> {
      set.remove(2);

    });

    Thread t2 = new Thread(() -> {
      set.remove(2);

    });

    t1.start();
    t2.start();

    try {
      t1.join();
      t2.join();
      assertFalse(set.size() == 8);
    } catch (Exception e) {
      System.out.println("error!");
    }

  }

  // NEW TEST: attempt to do the same stuff but with a class where all the methods
  // are synchronized
  @RepeatedTest(220)
  @Test
  void testNewSetAdd() {
    final boolean a;
    final boolean b;

    Thread t1 = new Thread(() -> {
      goodSet.add(23);
      goodSet.add(22);

    });

    Thread t2 = new Thread(() -> {
      goodSet.add(22);
      goodSet.add(4);

    });

    t1.start();
    t2.start();
    try {
      t1.join();
      t2.join();
      assertTrue(goodSet.size() == 3);
    } catch (Exception e) {
      System.out.println("went wrong");
    }

  }

  @RepeatedTest(100)
  @Test
  void testSemaphores() {

    // the semaphore used here has capacity 2.

    Thread t1 = new Thread(() -> {
      semaphore.release();
      semaphore.release();
      semaphore.release();
      semaphore.release();
      semaphore.release();
      semaphore.release();
      semaphore.release();

    });

    Thread t2 = new Thread(() -> {
      try {
        semaphore.acquire();

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    Thread t4 = new Thread(() -> {
      try {
        semaphore.acquire();

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    Thread t3 = new Thread(() -> {
      try {
        semaphore.acquire();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // Start the threads
    t1.start();
    t2.start();
    t3.start();
    t4.start();

    // Joining the threads

    try {
      t1.join();
      t2.join();
      t3.join();
      t4.join();
    } catch (InterruptedException e) {
      System.out.println("went bad");
    }

    int stateFinalVal = semaphore.getState();
    assertEquals(2, stateFinalVal);

    // i put 2 as the expected value but it could also be 0 or 1. Its just to show
    // that it wont stay within the range that is expected.
  }

}

// For week 2
// sestoft@itu.dk * 2015-10-29
package exercises02;

import java.util.concurrent.Semaphore;

public class TestLocking0 {
	public static void main(String[] args) {
		final int count = 1_000_000;
		Mystery m = new Mystery();
		Object lock = new Object(); // created a new object so that the threads share the same lock
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < count; i++)
				synchronized (lock) {
					m.addInstance(1);
				}
		});
		Thread t2 = new Thread(() -> {
			for (int i = 0; i < count; i++)
				synchronized (lock) {
					m.addStatic(1);
				}

		});
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException exn) {
		}
		System.out.printf("Sum is %f and should be %f%n", m.sum(), 2.0 * count);
	}
}

class Mystery {
	private static volatile double sum = 0;
	// public static Semaphore semaphore = new Semaphore(1); I attempted to add a
	// semaphore but it was easier to use a new object as a lock.

	public static void addStatic(double x) {
		sum += x;
	}

	public void addInstance(double x) {
		sum += x;
	}

	public static double sum() {
		return sum;
	}
}

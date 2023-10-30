package exercises07;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.checkerframework.checker.units.qual.radians;

public class MyHist implements Histogram {
    // fields
    AtomicIntegerArray count;

    // constructor
    public MyHist(int buckets) {
        /*
         * AtomicInteger[] atomicIntegers = new AtomicInteger[buckets];
         * for (int i = 0; i < buckets; i++) {
         * atomicIntegers[i] = new AtomicInteger(0); // Initialize with an initial value
         * if needed
         * }
         */
        count = new AtomicIntegerArray(buckets);

    }

    // this adds one ball to the specified bin
    @Override
    public void increment(int bin) {
        int oldval = count.get(bin);
        int newval = oldval + 1;

        while (!count.compareAndSet(bin, oldval, newval)) {
            // define the values again
            oldval = count.get(bin);
            newval = oldval + 1;
        }

    }

    @Override
    public int getCount(int bin) {
        return count.get(bin); // do i need more
    }

    @Override
    public int getSpan() {
        return count.length();
    }

    @Override
    public int getAndClear(int bin) {
        int oldval = count.get(bin);
        if (count.compareAndSet(bin, oldval, 0)) {
            return oldval;
        }
        return 0; // it only reaches here if someone set it before so we can safely return 0

    }

    public static void main(String[] args) {

    }

}

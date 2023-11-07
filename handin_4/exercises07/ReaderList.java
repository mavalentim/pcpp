package exercises07;

import java.util.HashSet;

public class ReaderList extends Holders {
    private final Thread thread;
    private final ReaderList next;
    private final ReaderList previous;

    /*
     * public ReaderList(ReaderList tail, ReaderList previous) {
     * this.thread = null;
     * this.next = (ReaderList) tail;
     * this.previous = previous;
     * }
     */

    public ReaderList(Thread t, Holders tail) {
        this.thread = t;
        this.next = (ReaderList) tail;
        this.previous = null;
    }

    public ReaderList getNext() {
        return next;
    }

    public ReaderList getPrevious() {
        return previous;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean contains1(Thread t) {
        while (this.getNext() != null) {
            if (this.getThread() == t) {
                return true;
            }
        }
        return false;
    }

    /*
     * public boolean contains2(Thread t) {
     * if (this.getThread() == t) {
     * return true;
     * }
     * return this.getNext().getThread() == t;
     * }
     */

    public ReaderList remove(Thread t) {
        if (this.thread == t) {
            return this.getNext();
        }

        if (this.thread == null) {
            return this;
        }

        return new ReaderList(t, this.getNext().remove(t));

    }

}
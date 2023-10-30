package exercises07;

import java.util.HashSet;

public class ReaderList extends Holders {
    public HashSet<Thread> readers;

    public ReaderList() {
        readers = new HashSet<>();
    }

    public void add(Thread thread) {
        readers.add(thread);
    }

    public void remove(Thread thread) {
        readers.remove(thread);
    }

    /*
     * public ReaderList(Thread thread) {
     * this.thread = thread;
     * this.next = null;
     * }
     * 
     * public void add(ReaderList newThread) {
     * next = new ReaderList(thread);
     * }
     * 
     * public void remove(ReaderList tobeRemoved) {
     * while (this.next != null) {
     * if (thread.equals(tobeRemoved.thread)) {
     * 
     * }
     * }
     * }
     */
}

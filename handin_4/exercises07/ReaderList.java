package exercises07;

public class ReaderList extends Holders {
    private final Thread thread;
    public ReaderList head;
    public ReaderList next;

    public ReaderList(Thread thread) {
        this.thread = thread;
        this.next = null;
    }

    public void add(ReaderList newThread) {
        next = new ReaderList(thread);
    }

    public void remove(ReaderList tobeRemoved) {
        while (this.next != null) {
            if (thread.equals(tobeRemoved.thread)) {

            }
        }
    }
}

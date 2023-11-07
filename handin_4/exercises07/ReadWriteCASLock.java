// For week 7
// raup@itu.dk * 10/10/2021
/*
 * To implement a working ReadWriteCASLock you must implement 
 * the reader list as an immutable linked list.

Add a constructor with the following signature:
ReaderList(Thread t, ReaderList tail)

Use this constructor to create a new larger instead of using your add method.

To implement the contains method, you should check if the head 
contains the thread.
If it does not, then make a recursive call to check if the rest of the 
list contains the thread (remember to handle the case where next is null)

For remove, you must check if the thread t to be removed is in the head.
If it isn't then create a new ReaderList like this:
new ReaderList(this.thread, remove(next)).
Again, remember to handle the case where next is null.
 */

package exercises07;

import java.util.concurrent.atomic.AtomicReference;

// Very likely you will need some imports here

class ReadWriteCASLock implements SimpleRWTryLockInterface {

  // first super important field is who the fuck is holding this
  private AtomicReference<Holders> holder = new AtomicReference<Holders>();

  public boolean readerTryLock() {
    // had to declare them before
    Thread thisThread;
    ReaderList thisThreadReaderList;

    if (!(holder.get() instanceof Writer)) { // if didnt get the lock cause theres a writer, quit, if because theres a
                                             // reader dont quit
      ReaderList expectedOldReaderList = null;
      ReaderList updatedReaderList = null;

      // do {
      // this has to be repeated in case theres another ReaderList being written
      thisThread = Thread.currentThread();
      Holders currentHolder = holder.get(); // idk if its another reader holding this
      thisThreadReaderList = new ReaderList(thisThread, currentHolder);
      if (Holders.contains(thisThread, currentHolder))
        throw new RuntimeException("Lock has already been acquired");
      return holder.compareAndSet(currentHolder, thisThreadReaderList);
      // thisThreadReaderList.add(thisThread); // set with only this thread
      // if (currentHolder instanceof ReaderList) { // if theres already a set of
      // threads there
      // then i can cast it
      // expectedOldReaderList = (ReaderList) currentHolder;
      // updatedReaderList = (ReaderList) currentHolder;
      // and add this readerList
      // updatedReaderList.add(thisThread);

      // }

      // i have to do the old one stuff again

      // } while (holder.compareAndSet(null, thisThreadReaderList)
      // || holder.compareAndSet((expectedOldReaderList, updatedReaderList));
      // return true;
    }
    return false;

  }

  public void readerUnlock() {

    ReaderList newestReaderList = (ReaderList) holder.get();;
    ReaderList updatedReaderList = null;
    do {
      Thread thisThread = Thread.currentThread();
      Holders currentHolder = holder.get();
      if (currentHolder instanceof ReaderList || Holders.contains(thisThread, currentHolder)) {
        //newestReaderList = (ReaderList) currentHolder;
        updatedReaderList = (ReaderList) ReaderList.remove(thisThread, currentHolder); 
        // newest readerlist we know

        //updatedReaderList.remove(thisThread);


      }

    } while (holder.compareAndSet(newestReaderList, updatedReaderList));

  }

  public boolean writerTryLock() {
    final Writer callingThread = new Writer(Thread.currentThread());
    if (holder.compareAndSet(null, callingThread)) {
      return true;
    }
    return false;

  }

  public void writerUnlock() throws Exception {
    final Writer callingThread = new Writer(Thread.currentThread());

    // ArrayList<Thread> newList = new ArrayList<Thread>();
    // newList.add(callingThread);

    if (!holder.compareAndSet(callingThread, null)) {
      throw new Exception("not my lock");
    }

  }

  private static abstract class Holders {
    abstract Thread getThread();

    abstract Holders getNext();

    public static boolean contains(Thread t, Holders list) {
      Holders next = list;
      do {
        if (next.getThread() == t)
          return true;
        next = list.getNext();
      } while (next != null);
      return false;
    }

    public static Holders remove(Thread t, Holders list) {
      Holders oldList = list;
      Holders newList = null;
      do {
        if (!(oldList.getThread() == t))
          newList = new ReaderList(oldList.getThread(), newList);
        oldList = oldList.getNext();
      } while (oldList != null);
      return newList;
    }
  }

  private static class ReaderList extends Holders {
    private final Thread thread;
    private final ReaderList next;

    public ReaderList(Thread t, Holders tail) {
      this.thread = t;
      this.next = (ReaderList) tail;
    }

    public ReaderList getNext() {
      return next;
    }

    public Thread getThread() {
      return thread;
    }
  }

  private static class Writer extends Holders {
    public final Thread thread;

    public Writer(Thread t) {
      this.thread = t;
    }

    public Thread getThread() {
      return thread;
    }

    public Writer getNext() {
      return null;
    }
  }

}
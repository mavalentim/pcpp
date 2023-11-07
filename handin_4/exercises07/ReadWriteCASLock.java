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
    Thread thisThread = Thread.currentThread();

    ReaderList expectedOldReaderList;
    ReaderList updatedReaderList;

    do {
      Holder currHolder = holder.get();
      if (currHolder instanceof ReaderList) {
        expectedOldReaderList = (ReaderList) currHolder;
        updatedReaderList = new ReaderList(thisThread, expectedOldReaderList);
      } else {
        return false;
      }
    } while (!holder.compareAndSet(expectedOldReaderList, updatedReaderList));

    return true;

  }

  public void readerUnlock() {
    // things that dont change go first
    Thread thisThread = Thread.currentThread();

    ReaderList updatedReaderList = null;
    do {

      Holder currHolder = holder.get();
      if (currHolder instanceof ReaderList) {
        ReaderList expectedOldReaderList = (ReaderList) currHolder;
        if (expectedOldReaderList.contains1(thisThread)) { // should only remove if it contains
          updatedReaderList = expectedOldReaderList.remove(thisThread);
        }
        throw new Exception("i cant unlock if i havent locked");

      } else { // not an instance of readerList means theres writer, this method should only
               // return then
        return;
      }

    } while (!holder.compareAndSet(certifiedRL_currentHolder, updatedReaderList));

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

}
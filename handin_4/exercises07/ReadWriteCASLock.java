// For week 7
// raup@itu.dk * 10/10/2021

package exercises07;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Very likely you will need some imports here

class ReadWriteCASLock implements SimpleRWTryLockInterface {

  // first super important field is who the fuck is holding this
  private AtomicReference<Holders> holder = new AtomicReference<Holders>();

  // This feels reentrant to me
  public boolean readerTryLock() {

    Thread thisThread;
    ReaderList thisThreadReaderList;
    ReaderList updatedReaderList;
    /* ReaderList prevReaderList; */

    do {
      thisThread = Thread.currentThread();
      thisThreadReaderList = new ReaderList(thisThread);
      Holders currentHolder = holder.get(); // idk if its another reader holding this
      if (currentHolder instanceof ReaderList) {
        // then i can cast it
        updatedReaderList = (ReaderList) currentHolder;
        // and add this readerList
        updatedReaderList.add(thisThreadReaderList);
        holder.set(updatedReaderList);
      }

    } while (!(holder.compareAndSet(null, thisThreadReaderList) || holder.get() instanceof ReaderList));
    return true;

  }

  public void readerUnlock() {

    do {
      Holders currentHolder = holder.get();
      if (currentHolder instanceof ReaderList) {
        // then i can cast it
        ReaderList updatedReaderList = (ReaderList) currentHolder;
        // and remove this readerList (method not fully implemented)
        updatedReaderList.remove(thisThreadReaderList);
        holder.set(updatedReaderList);
      }

    } while (holder.get() == null && holder.get() instanceof ReaderList);

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

// For week 7
// raup@itu.dk * 10/10/2021

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

      do {
        // this has to be repeated in case theres another ReaderList being written
        thisThread = Thread.currentThread();
        thisThreadReaderList = new ReaderList();
        thisThreadReaderList.add(thisThread); // set with only this thread
        Holders currentHolder = holder.get(); // idk if its another reader holding this
        if (currentHolder instanceof ReaderList) { // if theres already a set of threads there
          // then i can cast it
          expectedOldReaderList = (ReaderList) currentHolder;
          updatedReaderList = (ReaderList) currentHolder;
          // and add this readerList
          updatedReaderList.add(thisThread);

        }

        // i have to do the old one stuff again

      } while (holder.compareAndSet(null, thisThreadReaderList)
          || holder.compareAndSet(expectedOldReaderList, updatedReaderList));
      return true;
    }
    return false;

  }

  public void readerUnlock() {

    ReaderList newestReaderList = null;
    ReaderList updatedReaderList = null;
    do {
      Thread thisThread = Thread.currentThread();
      Holders currentHolder = holder.get();
      if (currentHolder instanceof ReaderList) {
        newestReaderList = (ReaderList) currentHolder;
        updatedReaderList = (ReaderList) currentHolder; // newest readerlist we know

        updatedReaderList.remove(thisThread);

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

}

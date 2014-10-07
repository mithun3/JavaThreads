package threads.advanced.lockSynchronize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteFIFOTest extends Object {
  private ByteFIFO fifo;  private byte[] srcData;

  public ByteFIFOTest() throws IOException {
    fifo = new ByteFIFO(20);

    makeSrcData();
    System.out.println("srcData.length=" + srcData.length);

    Runnable srcRunnable = new Runnable() {
        public void run() {
          src();
        }
      };
    Thread srcThread = new Thread(srcRunnable);
    srcThread.start();

    Runnable dstRunnable = new Runnable() {
        public void run() {
          dst();
        }
      };
    Thread dstThread = new Thread(dstRunnable);
    dstThread.start();
  }

  private void makeSrcData() throws IOException {
    String[] list = {
        "The first string is right here",
        "The second string is a bit longer and also right here",
        "The third string",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "0123456789",
        "The last string in the list"
      };

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(list);
    oos.flush();
    oos.close();
    srcData = baos.toByteArray();
  }

  private void src() {
    try {
      boolean justAddOne = true;
      int count = 0;

      while ( count < srcData.length ) {
        if ( !justAddOne ) {
          int writeSize = (int) ( 40.0 * Math.random() );
          writeSize = Math.min(writeSize, srcData.length - count);

          byte[] buf = new byte[writeSize];
          System.arraycopy(srcData, count, buf, 0, writeSize);
          fifo.add(buf);
          count += writeSize;

          System.out.println("just added " + writeSize + " bytes");
        } else {
          fifo.add(srcData[count]);
          count++;

          System.out.println("just added exactly 1 byte");
        }

        justAddOne = !justAddOne;
      }
    } catch ( InterruptedException x ) {
      x.printStackTrace();
    }
  }

  private void dst() {
    try {
      boolean justAddOne = true;
      int count = 0;
      byte[] dstData = new byte[srcData.length];

      while ( count < dstData.length ) {
        if ( !justAddOne ) {
          byte[] buf = fifo.removeAll();
          if ( buf.length > 0 ) {
            System.arraycopy(buf, 0, dstData, count, buf.length);
            count += buf.length;
          }

          System.out.println(
            "just removed " + buf.length + " bytes");
        } else {
          byte b = fifo.remove();
          dstData[count] = b;
          count++;

          System.out.println(
            "just removed exactly 1 byte");
        }

        justAddOne = !justAddOne;
      }

      System.out.println("received all data, count=" + count);

      ObjectInputStream ois = new ObjectInputStream(
          new ByteArrayInputStream(dstData));

      String[] line = (String[]) ois.readObject();

      for ( int i = 0; i < line.length; i++ ) {
        System.out.println("line[" + i + "]=" + line[i]);
      }
    } catch ( ClassNotFoundException x1 ) {
      x1.printStackTrace();
    } catch ( IOException iox ) {
      iox.printStackTrace();
    } catch ( InterruptedException x ) {
      x.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
      new ByteFIFOTest();
    } catch ( IOException iox ) {
      iox.printStackTrace();
    }
  }
}
class ByteFIFO extends Object {
  private byte[] queue;
  private int capacity;
  private int size;
  private int head;
  private int tail;

  public ByteFIFO(int cap) {
    capacity = ( cap > 0 ) ? cap : 1; // at least 1
    queue = new byte[capacity];
    head = 0;
    tail = 0;
    size = 0;
  }

  public int getCapacity() {
    return capacity;
  }

  public synchronized int getSize() {
    return size;
  }

  public synchronized boolean isEmpty() {
    return ( size == 0 );
  }

  public synchronized boolean isFull() {
    return ( size == capacity );
  }

  public synchronized void add(byte b) 
      throws InterruptedException {

    waitWhileFull();

    queue[head] = b;
    head = ( head + 1 ) % capacity;
    size++;

    notifyAll(); // let any waiting threads know about change
  }

  public synchronized void add(byte[] list) 
      throws InterruptedException {

    // For efficiency, the bytes are copied in blocks
    // instead of one at a time. As space becomes available,
    // more bytes are copied until all of them have been
    // added.

    int ptr = 0;

    while ( ptr < list.length ) {
      // If full, the lock will be released to allow 
      // another thread to come in and remove bytes.
      waitWhileFull();

      int space = capacity - size;
      int distToEnd = capacity - head;
      int blockLen = Math.min(space, distToEnd);

      int bytesRemaining = list.length - ptr;
      int copyLen = Math.min(blockLen, bytesRemaining);

      System.arraycopy(list, ptr, queue, head, copyLen);
      head = ( head + copyLen ) % capacity;
      size += copyLen;
      ptr += copyLen;

      // Keep the lock, but let any waiting threads 
      // know that something has changed.
      notifyAll();
    }
  }

  public synchronized byte remove() 
      throws InterruptedException {

    waitWhileEmpty();
    byte b = queue[tail];
    tail = ( tail + 1 ) % capacity;
    size--;

    notifyAll(); // let any waiting threads know about change

    return b;
  }

  public synchronized byte[] removeAll() {
    // For efficiency, the bytes are copied in blocks
    // instead of one at a time. 

    if ( isEmpty() ) {
      // Nothing to remove, return a zero-length
      // array and do not bother with notification
      // since nothing was removed.
      return new byte[0]; 
    }

    // based on the current size
    byte[] list = new byte[size]; 

    // copy in the block from tail to the end
    int distToEnd = capacity - tail;
    int copyLen = Math.min(size, distToEnd);
    System.arraycopy(queue, tail, list, 0, copyLen);

    // If data wraps around, copy the remaining data
    // from the front of the array.
    if ( size > copyLen ) {
      System.arraycopy(
          queue, 0, list, copyLen, size - copyLen);
    }

    tail = ( tail + size ) % capacity;
    size = 0; // everything has been removed

    // Signal any and all waiting threads that 
    // something has changed.
    notifyAll(); 

    return list; 
  }

  public synchronized byte[] removeAtLeastOne() 
      throws InterruptedException {

    waitWhileEmpty(); // wait for a least one to be in FIFO
    return removeAll();
  }

  public synchronized boolean waitUntilEmpty(long msTimeout) 
      throws InterruptedException {

    if ( msTimeout == 0L ) {
      waitUntilEmpty();  // use other method
      return true;
    }

    // wait only for the specified amount of time
    long endTime = System.currentTimeMillis() + msTimeout;
    long msRemaining = msTimeout;

    while ( !isEmpty() && ( msRemaining > 0L ) ) {
      wait(msRemaining);
      msRemaining = endTime - System.currentTimeMillis();
    }

    // May have timed out, or may have met condition, 
    // calc return value.
    return isEmpty();
  }

  public synchronized void waitUntilEmpty() 
      throws InterruptedException {

    while ( !isEmpty() ) {
      wait();
    }
  }

  public synchronized void waitWhileEmpty() 
      throws InterruptedException {

    while ( isEmpty() ) {
      wait();
    }
  }

  public synchronized void waitUntilFull() 
      throws InterruptedException {

    while ( !isFull() ) {
      wait();
    }
  }

  public synchronized void waitWhileFull() 
      throws InterruptedException {

    while ( isFull() ) {
      wait();
    }
  }
}
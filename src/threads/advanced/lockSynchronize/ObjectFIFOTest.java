package threads.advanced.lockSynchronize;

public class ObjectFIFOTest extends Object {

  private static void fullCheck(ObjectFIFO fifo) {
    try {
      // Sync'd to allow messages to print while
      // condition is still true.
      synchronized (fifo) {
        while (true) {
          fifo.waitUntilFull();
          print("FULL");
          fifo.waitWhileFull();
          print("NO LONGER FULL");
        }
      }
    } catch (InterruptedException ix) {
      return;
    }
  }

  private static void emptyCheck(ObjectFIFO fifo) {
    try {
      // Sync'd to allow messages to print while
      // condition is still true.
      synchronized (fifo) {
        while (true) {
          fifo.waitUntilEmpty();
          print("EMPTY");
          fifo.waitWhileEmpty();
          print("NO LONGER EMPTY");
        }
      }
    } catch (InterruptedException ix) {
      return;
    }
  }

  private static void consumer(ObjectFIFO fifo) {
    try {
      print("just entered consumer()");

      for (int i = 0; i < 3; i++) {
        synchronized (fifo) {
          Object obj = fifo.remove();
          print("DATA-OUT - did remove(), obj=" + obj);
        }
        Thread.sleep(3000);
      }

      synchronized (fifo) {
        boolean resultOfWait = fifo.waitUntilEmpty(500);
        print("did waitUntilEmpty(500), resultOfWait=" + resultOfWait
            + ", getSize()=" + fifo.getSize());
      }

      for (int i = 0; i < 3; i++) {
        synchronized (fifo) {
          Object[] list = fifo.removeAll();
          print("did removeAll(), list.length=" + list.length);

          for (int j = 0; j < list.length; j++) {
            print("DATA-OUT - list[" + j + "]=" + list[j]);
          }
        }
        Thread.sleep(100);
      }

      for (int i = 0; i < 3; i++) {
        synchronized (fifo) {
          Object[] list = fifo.removeAtLeastOne();
          print("did removeAtLeastOne(), list.length=" + list.length);

          for (int j = 0; j < list.length; j++) {
            print("DATA-OUT - list[" + j + "]=" + list[j]);
          }
        }
        Thread.sleep(1000);
      }

      while (!fifo.isEmpty()) {
        synchronized (fifo) {
          Object obj = fifo.remove();
          print("DATA-OUT - did remove(), obj=" + obj);
        }
        Thread.sleep(1000);
      }

      print("leaving consumer()");
    } catch (InterruptedException ix) {
      return;
    }
  }

  private static void producer(ObjectFIFO fifo) {
    try {
      print("just entered producer()");
      int count = 0;

      Object obj0 = new Integer(count);
      count++;
      synchronized (fifo) {
        fifo.add(obj0);
        print("DATA-IN - did add(), obj0=" + obj0);

        boolean resultOfWait = fifo.waitUntilEmpty(500);
        print("did waitUntilEmpty(500), resultOfWait=" + resultOfWait
            + ", getSize()=" + fifo.getSize());
      }

      for (int i = 0; i < 10; i++) {
        Object obj = new Integer(count);
        count++;
        synchronized (fifo) {
          fifo.add(obj);
          print("DATA-IN - did add(), obj=" + obj);
        }
        Thread.sleep(1000);
      }

      Thread.sleep(2000);

      Object obj = new Integer(count);
      count++;
      synchronized (fifo) {
        fifo.add(obj);
        print("DATA-IN - did add(), obj=" + obj);
      }
      Thread.sleep(500);

      Integer[] list1 = new Integer[3];
      for (int i = 0; i < list1.length; i++) {
        list1[i] = new Integer(count);
        count++;
      }

      synchronized (fifo) {
        fifo.addEach(list1);
        print("did addEach(), list1.length=" + list1.length);
      }

      Integer[] list2 = new Integer[8];
      for (int i = 0; i < list2.length; i++) {
        list2[i] = new Integer(count);
        count++;
      }

      synchronized (fifo) {
        fifo.addEach(list2);
        print("did addEach(), list2.length=" + list2.length);
      }

      synchronized (fifo) {
        fifo.waitUntilEmpty();
        print("fifo.isEmpty()=" + fifo.isEmpty());
      }

      print("leaving producer()");
    } catch (InterruptedException ix) {
      return;
    }
  }

  private static synchronized void print(String msg) {
    System.out.println(Thread.currentThread().getName() + ": " + msg);
  }

  public static void main(String[] args) {
    final ObjectFIFO fifo = new ObjectFIFO(5);

    Runnable fullCheckRunnable = new Runnable() {
      public void run() {
        fullCheck(fifo);
      }
    };

    Thread fullCheckThread = new Thread(fullCheckRunnable, "fchk");
    fullCheckThread.setPriority(9);
    fullCheckThread.setDaemon(true); // die automatically
    fullCheckThread.start();

    Runnable emptyCheckRunnable = new Runnable() {
      public void run() {
        emptyCheck(fifo);
      }
    };

    Thread emptyCheckThread = new Thread(emptyCheckRunnable, "echk");
    emptyCheckThread.setPriority(8);
    emptyCheckThread.setDaemon(true); // die automatically
    emptyCheckThread.start();

    Runnable consumerRunnable = new Runnable() {
      public void run() {
        consumer(fifo);
      }
    };

    Thread consumerThread = new Thread(consumerRunnable, "cons");
    consumerThread.setPriority(7);
    consumerThread.start();

    Runnable producerRunnable = new Runnable() {
      public void run() {
        producer(fifo);
      }
    };

    Thread producerThread = new Thread(producerRunnable, "prod");
    producerThread.setPriority(6);
    producerThread.start();
  }
}

class ObjectFIFO extends Object {
  private Object[] queue;

  private int capacity;

  private int size;

  private int head;

  private int tail;

  public ObjectFIFO(int cap) {
    capacity = (cap > 0) ? cap : 1; // at least 1
    queue = new Object[capacity];
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
    return (size == 0);
  }

  public synchronized boolean isFull() {
    return (size == capacity);
  }

  public synchronized void add(Object obj) throws InterruptedException {

    waitWhileFull();

    queue[head] = obj;
    head = (head + 1) % capacity;
    size++;

    notifyAll(); // let any waiting threads know about change
  }

  public synchronized void addEach(Object[] list) throws InterruptedException {

    //
    // You might want to code a more efficient
    // implementation here ... (see ByteFIFO.java)
    //

    for (int i = 0; i < list.length; i++) {
      add(list[i]);
    }
  }

  public synchronized Object remove() throws InterruptedException {

    waitWhileEmpty();

    Object obj = queue[tail];

    // don't block GC by keeping unnecessary reference
    queue[tail] = null;

    tail = (tail + 1) % capacity;
    size--;

    notifyAll(); // let any waiting threads know about change

    return obj;
  }

  public synchronized Object[] removeAll() throws InterruptedException {

    //
    // You might want to code a more efficient
    // implementation here ... (see ByteFIFO.java)
    //

    Object[] list = new Object[size]; // use the current size

    for (int i = 0; i < list.length; i++) {
      list[i] = remove();
    }

    // if FIFO was empty, a zero-length array is returned
    return list;
  }

  public synchronized Object[] removeAtLeastOne() throws InterruptedException {

    waitWhileEmpty(); // wait for a least one to be in FIFO
    return removeAll();
  }

  public synchronized boolean waitUntilEmpty(long msTimeout)
      throws InterruptedException {

    if (msTimeout == 0L) {
      waitUntilEmpty(); // use other method
      return true;
    }

    // wait only for the specified amount of time
    long endTime = System.currentTimeMillis() + msTimeout;
    long msRemaining = msTimeout;

    while (!isEmpty() && (msRemaining > 0L)) {
      wait(msRemaining);
      msRemaining = endTime - System.currentTimeMillis();
    }

    // May have timed out, or may have met condition,
    // calc return value.
    return isEmpty();
  }

  public synchronized void waitUntilEmpty() throws InterruptedException {

    while (!isEmpty()) {
      wait();
    }
  }

  public synchronized void waitWhileEmpty() throws InterruptedException {

    while (isEmpty()) {
      wait();
    }
  }

  public synchronized void waitUntilFull() throws InterruptedException {

    while (!isFull()) {
      wait();
    }
  }

  public synchronized void waitWhileFull() throws InterruptedException {

    while (isFull()) {
      wait();
    }
  }
}
package threads.advanced.lockSynchronize;

public class SimpleObjectFIFO extends Object {
  private Object[] queue;

  private int capacity;

  private int size;

  private int head;

  private int tail;

  public SimpleObjectFIFO(int cap) {
    capacity = (cap > 0) ? cap : 1; // at least 1
    queue = new Object[capacity];
    head = 0;
    tail = 0;
    size = 0;
  }

  public synchronized int getSize() {
    return size;
  }

  public synchronized boolean isFull() {
    return (size == capacity);
  }

  public synchronized void add(Object obj) throws InterruptedException {
    while (isFull()) {
      wait();
    }

    queue[head] = obj;
    head = (head + 1) % capacity;
    size++;

    notifyAll(); // let any waiting threads know about change
  }

  public synchronized Object remove() throws InterruptedException {
    while (size == 0) {
      wait();
    }

    Object obj = queue[tail];
    queue[tail] = null; // don't block GC by keeping unnecessary reference
    tail = (tail + 1) % capacity;
    size--;

    notifyAll(); // let any waiting threads know about change

    return obj;
  }

  public synchronized void printState() {
    StringBuffer sb = new StringBuffer();

    sb.append("SimpleObjectFIFO:\n");
    sb.append("       capacity=" + capacity + "\n");

    sb.append("           size=" + size);
    if (isFull()) {
      sb.append(" - FULL");
    } else if (size == 0) {
      sb.append(" - EMPTY");
    }
    sb.append("\n");

    sb.append("           head=" + head + "\n");
    sb.append("           tail=" + tail + "\n");

    for (int i = 0; i < queue.length; i++) {
      sb.append("       queue[" + i + "]=" + queue[i] + "\n");
    }

    System.out.print(sb);
  }

  public static void main(String[] args) {
    try {
      SimpleObjectFIFO fifo = new SimpleObjectFIFO(5);
      fifo.printState();

      fifo.add("S01");
      fifo.printState();

      fifo.add("S02");
      fifo.printState();

      fifo.add("S03");
      fifo.printState();

      Object obj = fifo.remove();
      System.out.println("just removed obj=" + obj);
      fifo.printState();

      fifo.add("S04");
      fifo.printState();

      fifo.add("S05");
      fifo.printState();

      fifo.add("S06");
      fifo.printState();
    } catch (InterruptedException x) {
      x.printStackTrace();
    }
  }

}
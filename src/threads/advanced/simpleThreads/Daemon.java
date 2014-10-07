package threads.advanced.simpleThreads;

class Daemons extends Thread {
  private Thread[] t = new Thread[10];

  public Daemons() {
    setDaemon(true);
    start();
  }

  public void run() {
    for (int i = 0; i < t.length; i++)
      t[i] = new DaemonSpawn(i);
    for (int i = 0; i < t.length; i++)
      System.out.println("t[" + i + "].isDaemon() = " + t[i].isDaemon());
    while (true)
      yield();
  }
}

class DaemonSpawn extends Thread {
  public DaemonSpawn(int i) {
    start();
    System.out.println("DaemonSpawn " + i + " started");
  }

  public void run() {
    while (true)
      yield();
  }
}

public class Daemon {

  public static void main(String[] args) throws Exception {
    Thread d = new Daemons();
    System.out.println("d.isDaemon() = " + d.isDaemon());
    // Allow the daemon threads to
    // finish their startup processes:
    Thread.sleep(1000);
  }
} ///:~
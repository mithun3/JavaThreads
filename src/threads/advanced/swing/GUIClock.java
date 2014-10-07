package threads.advanced.swing;

import java.applet.Applet;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class GUIClock extends Applet implements Sleeper {

  private AlarmClock clock;

  public void init() {
    clock = new AlarmClock();
  }

  public void start() {
    clock.letMeSleepFor(this, ONE_SECOND);
  }

  public void paint(Graphics g) {
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    DateFormat dateFormatter = DateFormat.getTimeInstance();
    g.drawString(dateFormatter.format(date), 5, 10);
  }

  public void wakeUp() {
    repaint();
    clock.letMeSleepFor(this, ONE_SECOND);
  }
}

interface Sleeper {
  public void wakeUp();

  public long ONE_SECOND = 1000; // in milliseconds
}

class AlarmClock {

  private static final int MAX_CAPACITY = 10;

  private static final int UNUSED = -1;

  private static final int NOROOM = -1;

  private Sleeper[] sleepers = new Sleeper[MAX_CAPACITY];

  private long[] sleepFor = new long[MAX_CAPACITY];

  public AlarmClock() {
    for (int i = 0; i < MAX_CAPACITY; i++)
      sleepFor[i] = UNUSED;
  }

  public synchronized boolean letMeSleepFor(Sleeper s, long time) {
    int index = findNextSlot();
    if (index == NOROOM) {
      return false;
    } else {
      sleepers[index] = s;
      sleepFor[index] = time;
      new AlarmThread(index).start();
      return true;
    }
  }

  private synchronized int findNextSlot() {
    for (int i = 0; i < MAX_CAPACITY; i++) {
      if (sleepFor[i] == UNUSED)
        return i;
    }
    return NOROOM;
  }

  private synchronized void wakeUpSleeper(int sleeperIndex) {
    sleepers[sleeperIndex].wakeUp();
    sleepers[sleeperIndex] = null;
    sleepFor[sleeperIndex] = UNUSED;
  }

  private class AlarmThread extends Thread {
    int mySleeper;

    AlarmThread(int sleeperIndex) {
      super();
      mySleeper = sleeperIndex;
    }

    public void run() {
      try {
        sleep(sleepFor[mySleeper]);
      } catch (InterruptedException e) {
      }
      wakeUpSleeper(mySleeper);
    }
  }
}
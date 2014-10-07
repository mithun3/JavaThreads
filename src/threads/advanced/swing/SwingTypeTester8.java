package threads.advanced.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SwingTypeTester8 extends JFrame implements CharacterSource {

    protected RandomCharacterGenerator producer;
    private AnimatedCharacterDisplayCanvas displayCanvas;
    private CharacterDisplayCanvas feedbackCanvas;
    private JButton quitButton;
    private JButton startButton;
    private JButton stopButton;
    private CharacterEventHandler handler;
    private ScoreLabel score;

    public SwingTypeTester8() {
        initComponents();
    }

    private void initComponents() {
        handler = new CharacterEventHandler();
        producer = new RandomCharacterGenerator();
        producer.setDone(true);
        producer.start();
        displayCanvas = new AnimatedCharacterDisplayCanvas(producer);
        feedbackCanvas = new CharacterDisplayCanvas(this);
        quitButton = new JButton();
        startButton = new JButton();
        stopButton = new JButton();
        score = new ScoreLabel(producer, this);

        Container pane = getContentPane();
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
        p1.add(displayCanvas);
        p1.add(feedbackCanvas);

        JPanel p2 = new JPanel();
        score.setText("      ");
        score.setFont(new Font("MONOSPACED", Font.BOLD, 30));
        p2.add(score);
        startButton.setText("Start");
        p2.add(startButton);
        stopButton.setText("Stop");
        stopButton.setEnabled(false);
        p2.add(stopButton);
        quitButton.setText("Quit");
        p2.add(quitButton);
        p1.add(p2);
        pane.add(p1, BorderLayout.NORTH);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                quit();
            }
        });
        feedbackCanvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (c != KeyEvent.CHAR_UNDEFINED)
                    newCharacter((int) c);
            }
        });
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                displayCanvas.setDone(false);
                producer.setDone(false);
                score.resetScore();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                feedbackCanvas.setEnabled(true);
                feedbackCanvas.requestFocus();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {      startButton.setEnabled(true);
                stopButton.setEnabled(false);
                producer.setDone(true);
                displayCanvas.setDone(true);
                feedbackCanvas.setEnabled(false);
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quit();
            }
        });
    }

    private void quit() {
        System.exit(0);
    }

    public void addCharacterListener(CharacterListener cl) {
        handler.addCharacterListener(cl);
    }

    public void removeCharacterListener(CharacterListener cl) {
        handler.removeCharacterListener(cl);
    }

    public void newCharacter(int c) {
        handler.fireNewCharacter(this, c);
    }

    public void nextCharacter() {
        throw new IllegalStateException("We don't produce on demand");
    }
    public static void main(String args[]) {
        new SwingTypeTester8().show();
    }
}
class RandomCharacterGenerator extends Thread implements CharacterSource {
    private static char[] chars;
    private static String charArray = "abcdefghijklmnopqrstuvwxyz0123456789";
    static {
        chars = charArray.toCharArray();
    }

    private Random random;
    private CharacterEventHandler handler;
    private boolean done = true;
    private Lock lock = new ReentrantLock();
    private Condition cv = lock.newCondition();

    public RandomCharacterGenerator() {
        random = new Random();
        handler = new CharacterEventHandler();
    }

    public int getPauseTime(int minTime, int maxTime) {
        return (int) (minTime + ((maxTime-minTime)*random.nextDouble()));
    }

    public int getPauseTime() {
        return getPauseTime(2000, 5500);
    }

    public void addCharacterListener(CharacterListener cl) {
        handler.addCharacterListener(cl);
    }

    public void removeCharacterListener(CharacterListener cl) {
        handler.removeCharacterListener(cl);
    }

    public void nextCharacter() {
        handler.fireNewCharacter(this,
                                (int) chars[random.nextInt(chars.length)]);
    }

    public void run() {
        try {
            lock.lock();
            while (true) {
                try {
                    if (done) {
                        cv.await();
                    } else {
                        nextCharacter();
                        cv.await(getPauseTime(), TimeUnit.MILLISECONDS);
                    }
                } catch (InterruptedException ie) {
                    return;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void setDone(boolean b) {
        try {
            lock.lock();
            done = b;

            if (!done) cv.signal();
        } finally {
            lock.unlock();
        }
    }
}
class ScoreLabel extends JLabel implements CharacterListener {
    private volatile int score = 0;
    private int char2type = -1;
    private CharacterSource generator = null, typist = null;
    private Lock scoreLock = new ReentrantLock();

    public ScoreLabel(CharacterSource generator, CharacterSource typist) {
        this.generator = generator;
        this.typist = typist;
        if (generator != null)
            generator.addCharacterListener(this);
        if (typist != null)
            typist.addCharacterListener(this);
    }

    public ScoreLabel() {
        this(null, null);
    }

    public void resetGenerator(CharacterSource newGenerator) {
        try {
            scoreLock.lock();
            if (generator != null)
                generator.removeCharacterListener(this);
            generator = newGenerator;
            if (generator != null)
                generator.addCharacterListener(this);
        } finally {
            scoreLock.unlock();
        }
    }

    public void resetTypist(CharacterSource newTypist) {    try {
            scoreLock.lock();
            if (typist != null)
                typist.removeCharacterListener(this);
            typist = newTypist;
            if (typist != null)
                typist.addCharacterListener(this);
        } finally {
            scoreLock.unlock();
        }
    }

    public void resetScore() {
       try {
           scoreLock.lock();
           score = 0;
           char2type = -1;
           setScore();
       } finally {
           scoreLock.unlock();
       }
    }

    private void setScore() {
  if (SwingUtilities.isEventDispatchThread())
      setText(Integer.toString(score));
  else try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
        setText(Integer.toString(score));
    }
      });
  } catch (InterruptedException ie) {
  } catch (InvocationTargetException ite) {}
    }

    public void newCharacter(CharacterEvent ce) {
        scoreLock.lock();
        try {
            if (ce.source == generator) {
                if (char2type != -1) {
                    score--;
                    setScore();
                }
                char2type = ce.character;
            }
            else {
                if (char2type != ce.character) {
                    score--;
                } else {
                    score++;
                    char2type = -1;
                }
            }
            setScore();
        } finally {
            scoreLock.unlock();
        }
    }
}
class AnimatedCharacterDisplayCanvas extends CharacterDisplayCanvas
             implements CharacterListener, Runnable {

    private volatile boolean done = false;
    private int curX;
    private Lock lock = new ReentrantLock();
    private Condition cv = lock.newCondition();
    private Thread timer = null;

    public AnimatedCharacterDisplayCanvas(CharacterSource cs) {
        super(cs);
    }

    public synchronized void newCharacter(CharacterEvent ce) {
        curX = 0;     tmpChar[0] = (char) ce.character;
        repaint();
    }

    public synchronized void paintComponent(Graphics gc) {
        if (tmpChar[0] == 0)
            return;
        Dimension d = getSize();
        int charWidth = fm.charWidth(tmpChar[0]);
        gc.clearRect(0, 0, d.width, d.height);
        gc.drawChars(tmpChar, 0, 1, curX++, fontHeight);
        if (curX > d.width - charWidth)
            curX = 0;
    }

    public void run() {
        try {
            lock.lock();
            while (true) {
                try {
                    if (done) {
                        cv.await();
                  } else {
                      repaint();
                      cv.await(100, TimeUnit.MILLISECONDS);
                  }
               } catch (InterruptedException ie) {
                   return;
               }
           }
        } finally {
            lock.unlock();
        }
    }

    public void setDone(boolean b) {
        try {
            lock.lock();
            done = b;
            if (timer == null) {
                timer = new Thread(this);
                timer.start();
           }
           if (!done) cv.signal();
        } finally {
            lock.unlock();
        }
    }
}
interface CharacterSource {
    public void addCharacterListener(CharacterListener cl);
    public void removeCharacterListener(CharacterListener cl);
    public void nextCharacter();
}    
class CharacterEvent {
    public CharacterSource source;
    public int character;

    public CharacterEvent(CharacterSource cs, int c) {
        source = cs;
        character = c;
    }
}
 class CharacterEventHandler {
    private Vector listeners = new Vector();

    public void addCharacterListener(CharacterListener cl) {
        listeners.add(cl);
    }

    public void removeCharacterListener(CharacterListener cl) {
        listeners.remove(cl);
    }

    public void fireNewCharacter(CharacterSource source, int c) {
        CharacterEvent ce = new CharacterEvent(source, c);
        CharacterListener[] cl = (CharacterListener[] )
                                 listeners.toArray(new CharacterListener[0]);
        for (int i = 0; i < cl.length; i++)
            cl[i].newCharacter(ce);
    }
}
class CharacterDisplayCanvas extends JComponent implements CharacterListener {
    protected FontMetrics fm;
    protected char[] tmpChar = new char[1];
    protected int fontHeight;

    public CharacterDisplayCanvas(CharacterSource cs) {
        setFont(new Font("Monospaced", Font.BOLD, 18));
        fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
        fontHeight = fm.getHeight();
        cs.addCharacterListener(this);
    }

    public void setCharacterListener(CharacterSource cs) {
        cs.addCharacterListener(this);
    }

    public Dimension preferredSize() {
        return new Dimension(fm.getMaxAscent() + 10,
                             fm.getMaxAdvance() + 10);
    }

    public synchronized void newCharacter(CharacterEvent ce) {
        tmpChar[0] = (char) ce.character;
        repaint();
    }

    protected synchronized void paintComponent(Graphics gc) {
        Dimension d = getSize();
        gc.clearRect(0, 0, d.width, d.height);
        if (tmpChar[0] == 0)
            return;
        int charWidth = fm.charWidth((int) tmpChar[0]);
        gc.drawChars(tmpChar, 0, 1,
                     (d.width - charWidth) / 2, fontHeight);
    }
}
interface CharacterListener {
    public void newCharacter(CharacterEvent ce);
} 
 
 

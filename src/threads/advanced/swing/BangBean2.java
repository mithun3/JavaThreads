package threads.advanced.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BangBean2 extends JPanel implements Serializable {
  private int xm, ym;

  private int cSize = 20; // Circle size

  private String text = "Bang!";

  private int fontSize = 48;

  private Color tColor = Color.RED;

  private ArrayList actionListeners = new ArrayList();

  public BangBean2() {
    addMouseListener(new ML());
    addMouseMotionListener(new MM());
  }

  public synchronized int getCircleSize() {
    return cSize;
  }  public synchronized void setCircleSize(int newSize) {
    cSize = newSize;
  }

  public synchronized String getBangText() {
    return text;
  }

  public synchronized void setBangText(String newText) {
    text = newText;
  }

  public synchronized int getFontSize() {
    return fontSize;
  }

  public synchronized void setFontSize(int newSize) {
    fontSize = newSize;
  }

  public synchronized Color getTextColor() {
    return tColor;
  }

  public synchronized void setTextColor(Color newColor) {
    tColor = newColor;
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.BLACK);
    g.drawOval(xm - cSize / 2, ym - cSize / 2, cSize, cSize);
  }

  // This is a multicast listener, which is more typically
  // used than the unicast approach taken in BangBean.java:
  public synchronized void addActionListener(ActionListener l) {
    actionListeners.add(l);
  }

  public synchronized void removeActionListener(ActionListener l) {
    actionListeners.remove(l);
  }

  // Notice this isn't synchronized:
  public void notifyListeners() {
    ActionEvent a = new ActionEvent(BangBean2.this,
        ActionEvent.ACTION_PERFORMED, null);
    ArrayList lv = null;
    // Make a shallow copy of the List in case
    // someone adds a listener while we're
    // calling listeners:
    synchronized (this) {
      lv = (ArrayList) actionListeners.clone();
    }
    // Call all the listener methods:
    for (int i = 0; i < lv.size(); i++)
      ((ActionListener) lv.get(i)).actionPerformed(a);
  }

  class ML extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      Graphics g = getGraphics();
      g.setColor(tColor);
      g.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
      int width = g.getFontMetrics().stringWidth(text);
      g.drawString(text, (getSize().width - width) / 2,
          getSize().height / 2);
      g.dispose();
      notifyListeners();
    }
  }

  class MM extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent e) {
      xm = e.getX();
      ym = e.getY();
      repaint();
    }
  }

  public static void main(String[] args) {
    BangBean2 bb = new BangBean2();
    bb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("ActionEvent" + e);
      }
    });
    bb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("BangBean2 action");
      }
    });
    bb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("More action");
      }
    });
    run(bb, 300, 300);
  }

  public static void run(JPanel panel, int width, int height) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(panel);
    frame.setSize(width, height);
    frame.setVisible(true);
  }

} ///:~
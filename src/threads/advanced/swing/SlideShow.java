package threads.advanced.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SlideShow extends JComponent {
  private BufferedImage[] slide;

  private Dimension slideSize;

  private volatile int currSlide;

  private Thread internalThread;

  private volatile boolean noStopRequested;

  public SlideShow() {
    currSlide = 0;
    slideSize = new Dimension(50, 50);
    buildSlides();

    setMinimumSize(slideSize);
    setPreferredSize(slideSize);
    setMaximumSize(slideSize);
    setSize(slideSize);

    noStopRequested = true;
    Runnable r = new Runnable() {
      public void run() {
        try {
          runWork();
        } catch (Exception x) {
          x.printStackTrace();
        }
      }
    };

    internalThread = new Thread(r, "SlideShow");
    internalThread.start();
  }

  private void buildSlides() {
    RenderingHints renderHints = new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    renderHints.put(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);

    slide = new BufferedImage[20];

    Color rectColor = Color.BLUE;
    Color circleColor = Color.YELLOW;

    for (int i = 0; i < slide.length; i++) {
      slide[i] = new BufferedImage(slideSize.width, slideSize.height,
          BufferedImage.TYPE_INT_RGB);

      Graphics2D g2 = slide[i].createGraphics();
      g2.setRenderingHints(renderHints);

      g2.setColor(rectColor);
      g2.fillRect(0, 0, slideSize.width, slideSize.height);

      g2.setColor(circleColor);

      int diameter = 0;
      if (i < (slide.length / 2)) {
        diameter = 5 + (8 * i);
      } else {
        diameter = 5 + (8 * (slide.length - i));
      }

      int inset = (slideSize.width - diameter) / 2;
      g2.fillOval(inset, inset, diameter, diameter);

      g2.setColor(Color.black);
      g2.drawRect(0, 0, slideSize.width - 1, slideSize.height - 1);

      g2.dispose();
    }
  }

  public void paint(Graphics g) {
    g.drawImage(slide[currSlide], 0, 0, this);
  }

  private void runWork() {
    while (noStopRequested) {
      try {
        Thread.sleep(100); // 10 frames per second
        currSlide = (currSlide + 1) % slide.length;
        repaint();
      } catch (InterruptedException x) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public void stopRequest() {
    noStopRequested = false;
    internalThread.interrupt();
  }

  public boolean isAlive() {
    return internalThread.isAlive();
  }

  public static void main(String[] args) {
    SlideShow ss = new SlideShow();

    JPanel p = new JPanel(new FlowLayout());
    p.add(ss);

    JFrame f = new JFrame("SlideShow");
    f.setContentPane(p);
    f.setSize(250, 150);
    f.setVisible(true);
  }
}
/**
 * 
 */
package it.unibo.oop.lab.reactivegui02;

import java.awt.FlowLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class ConcurrentGUI {

    public ConcurrentGUI() {
        final JFrame mainFrame = new JFrame("Concurrent GUI");

        final JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        final JLabel counterLabel = new JLabel("0");
        final JButton upButton = new JButton("up");
        final JButton downButton = new JButton("down");
        final JButton stopButton = new JButton("stop");

        final Agent counterAgent = new Agent();

        /*
         * Add a listener to the counter agent
         */
        counterAgent.addListener(pChangeEvent -> counterLabel.setText(String.valueOf(pChangeEvent.getNewValue())));

        /*
         * Start the counter agent
         */
        counterAgent.start();

        upButton.addActionListener(event -> counterAgent.doIncrement());
        downButton.addActionListener(event -> counterAgent.doDecrement());
        stopButton.addActionListener(event -> {
            counterAgent.stopCounting();
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
        });

        mainPanel.add(counterLabel);
        mainPanel.add(upButton);
        mainPanel.add(downButton);
        mainPanel.add(stopButton);

        mainFrame.setContentPane(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private static class Agent extends Thread {

        private volatile boolean up = true;
        private volatile boolean stop;
        private volatile int counter;
        /*
         * I use the property change support to handle the value change of the counter
         */
        private final PropertyChangeSupport pChangeSupport = new PropertyChangeSupport(this);

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                final int oldValue = this.counter;
                if (this.up) {
                    this.counter++;
                } else {
                    this.counter--;
                }
                /*
                 * Fire the property change for the counter
                 */
                this.pChangeSupport.firePropertyChange("counter", oldValue, this.counter);
            }
        }

        /**
         * Stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
         * Start incrementing.
         */
        public void doIncrement() {
            this.up = true;
        }

        /**
         * Start decrementing.
         */
        public void doDecrement() {
            this.up = false;
        }

        /**
         * Get the current counter.
         * 
         * @return the current counter
         */
        public synchronized int getCounter() {
            return this.counter;
        }

        /**
         * Add a listener for the property change.
         * 
         * @param listener
         *                     listener to add
         */
        public void addListener(final PropertyChangeListener listener) {
            this.pChangeSupport.addPropertyChangeListener(listener);
        }

    }
}

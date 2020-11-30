/**
 * 
 */
package it.unibo.oop.lab.reactivegui02;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class ConcurrentGUI {

    private final JFrame mainFrame;
    private final Agent counterAgent;

    public ConcurrentGUI() {
        this.mainFrame = new JFrame("Concurrent GUI");

        final JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        final JLabel counterLabel = new JLabel("0");
        final JButton upButton = new JButton("up");
        final JButton downButton = new JButton("down");
        final JButton stopButton = new JButton("stop");

        this.counterAgent = new Agent();

        /*
         * Add a listener to the counter agent
         */
        this.counterAgent.addListener(pChangeEvent -> {
            if ("counter".equals(pChangeEvent.getPropertyName())) {
                counterLabel.setText(String.valueOf(pChangeEvent.getNewValue()));
            } else {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });

        /*
         * Start the counter agent
         */
        this.counterAgent.start();

        upButton.addActionListener(event -> this.counterAgent.doIncrement());
        downButton.addActionListener(event -> this.counterAgent.doDecrement());
        stopButton.addActionListener(event -> {
            this.counterAgent.stopCounting();
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            stopButton.setEnabled(false);
        });

        mainPanel.add(counterLabel);
        mainPanel.add(upButton);
        mainPanel.add(downButton);
        mainPanel.add(stopButton);

        this.mainFrame.setContentPane(mainPanel);
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.pack();
        this.mainFrame.setVisible(true);
    }

    protected JFrame getMainFrame() {
        return this.mainFrame;
    }

    protected Agent getCounterAgent() {
        return this.counterAgent;
    }

}

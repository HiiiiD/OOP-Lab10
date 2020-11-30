/**
 * 
 */
package it.unibo.oop.lab.reactivegui03;

import it.unibo.oop.lab.reactivegui02.AbstractObservableAgent;
import it.unibo.oop.lab.reactivegui02.ConcurrentGUI;

/**
 *
 */
public class AnotherConcurrentGUI extends ConcurrentGUI {

    private static final int TIME_TO_WAIT = 10_000;

    public AnotherConcurrentGUI() {
        super();
        final AbstractObservableAgent agent = new AbstractObservableAgent() {

            @Override
            public void run() {
                try {
                    Thread.sleep(TIME_TO_WAIT);
                    this.getPropertyChangeSupport().firePropertyChange("isFinished", false, true);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };

        agent.addListener(event -> {
            this.getCounterAgent().stopCounting();
        });
        agent.start();

    }
}

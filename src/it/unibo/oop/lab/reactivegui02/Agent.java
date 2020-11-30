/**
 * 
 */
package it.unibo.oop.lab.reactivegui02;

/**
 *
 */
public class Agent extends AbstractObservableAgent {

    private volatile boolean up = true;
    private volatile boolean stop;
    private volatile int counter;

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
            this.getPropertyChangeSupport().firePropertyChange("counter", oldValue, this.counter);
        }
    }

    /**
     * Stop counting.
     */
    public void stopCounting() {
        this.stop = true;
        this.getPropertyChangeSupport().firePropertyChange("stop", false, true);
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
}

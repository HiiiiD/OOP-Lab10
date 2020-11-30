/**
 * 
 */
package it.unibo.oop.lab.reactivegui02;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Class for an observable agent.
 */
public abstract class AbstractObservableAgent extends Thread implements ObservableObject {

    /*
     * I use the property change support to handle the value change of the counter
     */
    private final PropertyChangeSupport pChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add a listener for the property change.
     * 
     * @param listener
     *                     listener to add
     */
    public void addListener(final PropertyChangeListener listener) {
        this.pChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pChangeSupport;
    }
}

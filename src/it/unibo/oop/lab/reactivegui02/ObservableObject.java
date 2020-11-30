/**
 * 
 */
package it.unibo.oop.lab.reactivegui02;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Interface to implement an object that can emit events.
 */
public interface ObservableObject {

    /**
     * Get the property change support.
     * 
     * @return the current property change support
     */
    PropertyChangeSupport getPropertyChangeSupport();

    /**
     * Add a listener to the property change support.
     * 
     * @param listener
     *                     listener to add
     */
    default void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.getPropertyChangeSupport().addPropertyChangeListener(listener);
    }
}

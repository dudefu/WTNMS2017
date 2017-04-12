package com.jhw.adm.client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jhw.adm.client.util.ThreadUtils;

public class ViewModel {
	
	public ViewModel() {
		propertySupport = new PropertyChangeSupport(this);
		observable = new AsynObservable();
		executorService = Executors.newSingleThreadExecutor(ThreadUtils.createThreadFactory(ViewModel.class.getSimpleName()));
	}

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners()) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(l);
                break;
            }
        }
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners(propertyName)) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(propertyName, l);
                break;
            }
        }
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
    	Runnable notify = new Runnable() {
			@Override
			public void run() {
				propertySupport.firePropertyChange(propertyName, oldValue, newValue);
			}
		};
		executorService.execute(notify);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertySupport.getPropertyChangeListeners();
    }
    
    public void addObserver(Observer o) {
    	observable.addObserver(o);
    }

    public void removeObserver(Observer o) {
    	observable.deleteObserver(o);
    }
    
    public void notifyObservers() {
    	observable.willChange();
    	observable.asynNotifyObservers(null);
    }
    
    public void notifyObservers(Object arg) {
    	observable.willChange();
    	observable.asynNotifyObservers(arg);
    }

	private final ExecutorService executorService;
    private final PropertyChangeSupport propertySupport;
    private final AsynObservable observable;
    
    private class AsynObservable extends Observable {
    	
    	public void willChange() {
    		setChanged();
    	}
    	
    	public void asynNotifyObservers(final Object arg) {
    		Runnable notify = new Runnable() {
    			@Override
    			public void run() {
    				AsynObservable.this.notifyObservers(arg);
    			}
    		};
    		executorService.execute(notify);
    	}
    }
}
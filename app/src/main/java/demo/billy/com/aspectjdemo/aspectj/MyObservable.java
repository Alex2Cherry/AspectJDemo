package demo.billy.com.aspectjdemo.aspectj;

import java.util.Observable;

/**
 * @author billy.qi
 * @since 16/12/5 09:51
 */
public class MyObservable extends Observable {

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}

package demo.billy.com.aspectjdemo;

import android.app.Application;

import java.util.Observable;
import java.util.Observer;

import demo.billy.com.aspectjdemo.aspectj.MyObservable;

/**
 * @author billy.qi
 * @since 16/12/2 18:35
 */
public class MyApplication extends Application {

    static MyApplication instance;

    Observable observable = new MyObservable();

    public static MyApplication get() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observable.deleteObserver(observer);
    }

    public void notifyObserver(String str) {
        observable.notifyObservers(str);
    }
}

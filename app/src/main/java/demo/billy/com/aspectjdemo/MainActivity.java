package demo.billy.com.aspectjdemo;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private TextView textView;

    private final LinkedList<String> list = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.get().addObserver(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.content);
        showInContent(null);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.get().notifyObserver("------------------------------");
                showToast(view);
                Util.show("button is clicked!");
            }
        });
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showInContent(String str) {
        if (textView != null) {
            if (!TextUtils.isEmpty(str)) {
                str += '\n';
            } else {
                str = "";
            }
            if (!list.isEmpty()) {
                synchronized (list) {
                    StringBuilder sb = new StringBuilder();
                    while(!list.isEmpty()) {
                        sb.append(list.removeFirst()).append('\n');
                    }
                    str += sb.toString();
                }
            }
            str += textView.getText();
            textView.setText(str);
        } else {
            if (!TextUtils.isEmpty(str)) {
                synchronized (list) {
                    list.addFirst(str);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.get().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o != null && o instanceof String) {
            showInContent((String) o);
        }
    }

    public void showToast(View view) {
        System.out.println("showToast(view) called!");
        Toast.makeText(this, "clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

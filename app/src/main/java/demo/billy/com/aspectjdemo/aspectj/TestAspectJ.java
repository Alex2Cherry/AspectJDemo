package demo.billy.com.aspectjdemo.aspectj;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.text.NumberFormat;

import demo.billy.com.aspectjdemo.MyApplication;

/**
 * AspectJInAndroid示例
 *
 *  AspectJ官方网站:       http://www.eclipse.org/aspectj/
 *  AspectJ文档:          http://www.eclipse.org/aspectj/doc/released/aspectj5rt-api/index.html
 *  AspectJ类库参考文档:   http://www.eclipse.org/aspectj/doc/released/runtime-api/index.html
 *
 * AspectJ框架实现原理简单介绍:
 *      http://blog.csdn.net/zhao9tian/article/details/37762389
 *      https://my.oschina.net/u/1458864/blog/287389
 * AspectJ使用介绍:
 *      http://www.2cto.com/kf/201605/512821.html
 *      https://yq.aliyun.com/articles/58725
 * 深入理解: http://blog.csdn.net/innost/article/details/49387395
 * 方法替换: http://xiaoyaozjl.iteye.com/blog/2126554
 * pointcut: http://jinnianshilongnian.iteye.com/blog/1415606
 * @author billy.qi
 * @since 16/12/2 16:38
 */
@Aspect
public class TestAspectJ {
    private int index = 1;
    private static final String TAG = "ASPECTJ";
    
    private static final String ALL_METHOD = "(* demo.billy.com.aspectjdemo..*.*(..))";
    //解决显示-aop的死循环,要过滤以下方法(死循环是由于本demo需要在UI上显示调用信息的代码同样会触发aop导致的,一般程序中不会出现)
    private static final String EXCLUDE_1 = "(* demo.billy.com.aspectjdemo.MainActivity.showInContent(..))";
    private static final String EXCLUDE_2 = "(* demo.billy.com.aspectjdemo.MainActivity.update(..))";
    private static final String EXCLUDE_3 = "(* demo.billy.com.aspectjdemo.MyApplication.notifyObserver(..))";
    private static final String EXCLUDE_4 = "(* demo.billy.com.aspectjdemo.MyApplication.get(..))";
    private static final String EXCLUDE_5 = "(* demo.billy.com.aspectjdemo.aspectj..*.*(..))";
    private static final String EXCLUDE_CALLS = "call" + EXCLUDE_1
            + " || call" + EXCLUDE_2
            + " || call" + EXCLUDE_3
            + " || call" + EXCLUDE_4
            + " || call" + EXCLUDE_5
            ;
    private static final String EXCLUDE_EXECUTIONS = "execution" + EXCLUDE_1
            + " || execution" + EXCLUDE_2
            + " || execution" + EXCLUDE_3
            + " || execution" + EXCLUDE_4
            + " || execution" + EXCLUDE_5
            ;


    //定义调用点的pointcut
    @Pointcut("call" + ALL_METHOD)
    public void beforeMethod(){}
    @Pointcut(EXCLUDE_CALLS) public void beforeMethodExclude(){}

    @Before("beforeMethod() && !beforeMethodExclude()")
    public void beforeCall(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String msg = "before->" + signature.toShortString();
        log(msg);
    }

    //定义执行点的pointcut
    @Pointcut("execution" + ALL_METHOD)
    public void aroundMethod(){}
    @Pointcut(EXCLUDE_EXECUTIONS)
    public void aroundMethodExclude(){}

    @Around("aroundMethod() && !aroundMethodExclude()")
    public Object aroundCall(ProceedingJoinPoint joinPoint) throws Throwable {
        int myIndex = index++;
        Signature signature = joinPoint.getSignature();
        TimeWatcher watcher = new TimeWatcher();
        log(myIndex + "--start->" + signature.toShortString());
        watcher.start();
        Object proceed = null;
        if (!signature.getName().equals("showToast")) {//这样replaceShowToast放在此方法下面就执行不到,放到此方法上面就能执行
            proceed = joinPoint.proceed();//调用原方法
        }
        watcher.stop();
        long totalTimeInNano = watcher.getTotalTimeInNano();
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        String format = numberInstance.format(totalTimeInNano);
        log(myIndex + "--end->" + signature.toShortString() + " : time( " + format + " ns)");
        return proceed;
    }

    //方法替换
    @Pointcut("execution(* demo.billy.com.aspectjdemo.MainActivity.showToast(..))") public void aroundShowToast(){}

    @Around("aroundShowToast()")//使用Around Advise但不调用原方法，等同于覆盖
    public void replaceShowToast(final ProceedingJoinPoint joinPoint) {

        final String method = joinPoint.getSignature().getName();
        final Object[] args = joinPoint.getArgs();
        if ("showToast".equals(method) && args.length > 0 && args[0] instanceof View) {
            //没有调用joinPoint.proceed();
            //在这里替换了MainActivity.showToast方法
            View view = (View) args[0];
            Toast.makeText(MyApplication.get(), "id=" + view.getId(), Toast.LENGTH_SHORT).show();
        }
    }

    //匹配所有activity的子类的生命周期方法
    @Pointcut("execution(* android.app.Activity+.on*(..))")
    public void afterActivity() {}

    @After("afterActivity()")
    public void callbeforeActivity(JoinPoint joinPoint) {
        String msg = "-------after Activity----------" + joinPoint.getSignature().toShortString();
        log(msg);
    }

    private void log(String msg) {
        Log.e(TAG, msg);
        MyApplication application = MyApplication.get();
        if (application != null) {
            application.notifyObserver(msg);
        }
    }

    //不写累赘的Pointcut,直接写advice
    @Before("execution(* android.view.View.OnClickListener+.on*(..))")
    public void callbeforeOnClick(JoinPoint joinPoint) {
        String msg = "------before  OnClickListener-----------" + joinPoint.getSignature().toShortString();
        log(msg);
    }

}

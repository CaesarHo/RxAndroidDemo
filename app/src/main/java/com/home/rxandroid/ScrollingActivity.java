package com.home.rxandroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;


public class ScrollingActivity extends AppCompatActivity {
    public final static String TAG = "ScrollingActivity";
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.button_run_scheduler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRunSchedulerExampleButtonClicked();
            }
        });


        /**
         * scan()变换
         * scan操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。
         * 它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据。它持续进行这个过程来产生剩余的数据序列。
         */
        findViewById(R.id.button_run_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scan 会将输入的第一个元素当作参数做一个函数运算(函数由你实现，规定需要两个参数，此时另一个默认没有)，然后发射结果
                // 同时，运算结果会被当作函数的与第二个参数与第二个元素再进行函数运算，完成后发射结果
                // 然后将这个结果与第三个元素作为函数的参数再次运算...直到最后一个元素

                Observable.just(1, 2, 3, 4).scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        //integer是第一个元素或上一次计算的结果，integer2是下一轮运算中新的序列中元素
                        Log.d(TAG, "scan call   integer:" + integer + "   integer2:" + integer2);
                        return integer + integer2;
                    }
                }).subscribe(onSubject());
            }
        });


        /**
         * flatMap()实现双重变换
         * flatMap()将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable。
         * 即：第一次转换时，它依次将输入的数据转换成一个Observable，然后将这些Observable发射的数据集中到一个Observable里依次发射出来
         */
        findViewById(R.id.button_run_flatmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flatMap可以实现一个双重转换，在它的回调方法中会返回一个observable对象，但它并不会直接发射这个对象
                //而是将这个observable对象要发射的值 集中到一个新的observable对象中依次发射
                //如本例，第一层Observable依次发射两个数组，经过flatmap转换之后，变成变成两个依次发射数组元素的observable
                // 最后在subscriber中接收到的直接是整型数，等于将两个数组"铺开"了，直接发射整数，这就是大概地"flat"的含义吧
                // flatMap方法可以很灵活的使用，实现双重变换，满足很多不同情况下的需求,比如处理嵌套的异步代码等，非常棒!
                Integer[] array1 = {1, 2, 3, 4}, array2 = {5, 6, 7, 8};
                Observable.just(array1, array2).flatMap(new Function<Integer[], Observable<?>>() {
                    @Override
                    public Observable<?> apply(@NonNull Integer[] integers) throws Exception {
                        Observable observable = Observable.fromArray(integers);
                        return observable;
                    }
                }).subscribe(onSubject());
            }
        });

        /**
         * 除了多样的Observable创建方式，RxJava还有一个神奇的操作就是变换。通过自己定义的方法，
         * 你可以将输入的值变换成另一种类型再输出(比如输入url，输出bitmap)，单一变换、批量变换、甚至实现双重变换，嵌套两重异步操作！
         * map()方法做转换
         */
        findViewById(R.id.button_run_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
//                        //将文件路径转换为bitmap发出 观察者直接收到bitmap进行处理
//                        Observable observable = Observable.just(imgFilePath);
//                        observable.map(new Function() {
//                            @Override
//                            public Bitmap call(String imgFilePath) {
//                                return getBitmapFromAssets(imgFilePath);
//                            }
//                        }).subscribeOn(Schedulers.immediate())//当前线程(子线程)发布
//                                .observeOn(AndroidSchedulers.mainThread())//UI线程执行(更新图片)
//                                .subscribe(new Subscriber<Bitmap>() {
//                                    @Override
//                                    public void onCompleted() {
//                                        Log.i(TAG, "observable.map(..)  onCompleted");
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        Log.i(TAG, "observable.map(..)  onError" + e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onNext(Bitmap bitmap) {
//                                        //显示图片
//                                        iv.setImageBitmap(bitmap);
//                                    }
//                                });
                    }
                };
                new Thread(run).start();
            }
        });


        /**
         * range发射从n到m的整数序列，repeat可以指定重复次数，以上发射的次序为：3，4，5，6，7，3，4，5，6，7。
         * 这里用到的Action0和Action1是两个可以替代Subscriber的接口，具体可以参见相关文档和源码实现，这里不深入介绍。
         * 其他还有Interval、Defer、Start等方法就不一一介绍了，本文主要是帮助初次接触的童鞋入门，RxJava的操作符非常丰富，
         * 这里很难一一说明，更多的内容要还需要大家自己去熟悉和探究。
         */
        findViewById(R.id.button_run_range).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //range 发射从n到m的整数序列 可以指定Scheduler设置执行方法运行的线程
                //repeat方法可以指定重复触发的次数
                Observable rangeObservable = Observable.range(3, 7).repeat(2);
                rangeObservable.subscribe(onSubject());
            }
        });

        /**
         * timer有定时的作用，延时发送一个值０
         */
        findViewById(R.id.button_run_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer()创建一个Observable，它在一个给定的延迟后发射一个特殊的值 设定执行方法在UI线程执行
                //延时两秒后发射值
                //实测 延时2s后发送了一个0
                Observable timerObservable = Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
                timerObservable.subscribe(onSubject());
            }
        });


        /**
         * just直接接收object作为参数，原样发射出来
         */
        findViewById(R.id.button_run_just).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Just类似于From，但是From会将数组或Iterable的元素具取出然后逐个发射，而Just只是简单的原样发射，将数组或Iterable当做单个数据。
                //Just接受一至九个参数，返回一个按参数列表顺序发射这些数据的Observable
                Observable justObservable = Observable.just(1, "someThing", false, 3.256f, new Teacher("Wade", 25, "NewYork"));
                justObservable.subscribe(onSubject());
            }
        });

        /**
         * 用from方法创建Observable，可以传入一个数组，或者一个继承了Iterable的类的对象作为参数，也就是说，
         * java中常用的数据结构如List、Map等都可以直接作为参数传入from()方法用以构建Observable。
         * 这样，当Observable发射数据时，它将会依次把序列中的元素依次发射出来。
         */
        findViewById(R.id.button_run_from).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Teacher为一个数据Bean，包含姓名，年龄，住址三个字段
                List<Teacher> teachers = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    teachers.add(new Teacher("name" + i, i, "place" + i));
                }
                //from方法支持继承了Interable接口的参数，所以常用的数据结构(Map、List..)都可以转换
                Observable fromObservale = Observable.fromArray(teachers);
                fromObservale.subscribe(onSubject());
            }
        });


        //fromArray,just,timer,range,scan
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    public Subject onSubject() {
        Subject subject = new Subject() {
            @Override
            public boolean hasObservers() {
                return false;
            }

            @Override
            public boolean hasThrowable() {
                return false;
            }

            @Override
            public boolean hasComplete() {
                return false;
            }

            @Override
            public Throwable getThrowable() {
                return null;
            }

            @Override
            protected void subscribeActual(Observer observer) {

            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {
                //依次接收到teachers中的对象
                Log.d(TAG, "onNext:" + o.toString());
                if (o instanceof Teacher) {
                    Teacher teacher = (Teacher) o;
                    //依次接收到teachers中的对象
                    Log.d(TAG, " onNext2:" + teacher.getI() + "," + teacher.getString1() + "," + teacher.getString2());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onCompleted");
            }
        };
        return subject;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    void onRunSchedulerExampleButtonClicked() {
        disposables.add(sampleObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(String string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                }));
    }

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                // Do some long running operation
                SystemClock.sleep(1000);
                return Observable.just("one", "two", "three", "four", "five");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

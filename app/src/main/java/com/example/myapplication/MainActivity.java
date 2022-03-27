package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
public static String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Observable<Task>taskObservable=Observable. //create a new Observable object
                                      fromIterable(DataSource.createTasksList()).//iterate through the list we pass
                                      subscribeOn(Schedulers.io()).
              //here i freeze the thread in backgroud
               filter(new Predicate<Task>() {
                   @Override
                   public boolean test(Task task) throws Throwable {
                      Log.d(TAG,"Test: "+Thread.currentThread().getName());
                      try{
                          Thread.sleep(2000);
                      }catch (InterruptedException e){
                          e.printStackTrace();
                      }
                      return task.isComplete();
                   }
               }).
                //any one what to see the result subcribe to this thread
                observeOn(AndroidSchedulers.mainThread());
//Observer subscribe to the mainthread
       //Observe been see in main thread where nothing been frozen and nothing been sleep

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG,"OnSubscribe called");
            }

            @Override
            public void onNext(@NonNull Task task) {
Log.d(TAG,"onNext: "+Thread.currentThread().getName());
Log.d(TAG,"OnNext: "+task.getDescription());
//we cannot Sleep Thread Here because it is work on main thread and it will freeze the Ui
            }

            @Override
            public void onError(@NonNull Throwable e) {
Log.e(TAG,"OnError: "+e);
            }

            @Override
            public void onComplete() {
Log.d(TAG,"OnComplete: Called");
            }
        });
    }
}
/**
 A lot of people don't know there's a difference between RxJava and RxAndroid. It's not much a of a difference, but it's recommended to get the most recent RxJava dependency, along with the most recent RxAndroid dependency.


 Here are the main things you need to know about the difference between RxJava and RxAndroid:
 RxAndroid contains reactive components that make using RxJava in Android easier and hassle free. More specifically it provides a Scheduler that can schedule tasks on the main thread or any other looper - aka any thread. (It makes threading on Android very simple).

 You could use just the RxAndroid dependency, but the releases of RxAndroid updates are less frequent. To make sure you're up-to-date with the newest RxJava components, use the newest RxJava dependency.

 */
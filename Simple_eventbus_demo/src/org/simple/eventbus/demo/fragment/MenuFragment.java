/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.simple.eventbus.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subcriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.demo.R;
import org.simple.eventbus.demo.bean.Person;

import java.util.Random;

/**
 * @author mrsimple
 */
public class MenuFragment extends Fragment {

    public static final String CLICK_TAG = "click_user";

    PostThread[] threads = new PostThread[4];

    TextView mUserNameTv;
    TextView mTimerTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);

        mUserNameTv = (TextView) rootView.findViewById(R.id.click_tv);
        mTimerTv = (TextView) rootView.findViewById(R.id.timer_tv);

        // 发布事件
        rootView.findViewById(R.id.my_post_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Person("Mr.Simple" + new Random().nextInt()));
            }
        });

        // 发布移除事件的按钮
        rootView.findViewById(R.id.my_remove_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 移除用户
                        EventBus.getDefault().post(new Person("User - 1"),
                                "remove");
                    }
                });

        // 发布异步事件的按钮
        rootView.findViewById(R.id.my_post_async_event_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 将目标函数执行在异步线程中
                        EventBus.getDefault().post(new Person("mr.simple-3"), "async");
                    }
                });

        for (int i = 0; i < 4; i++) {
            threads[i] = new PostThread(i);
            threads[i].start();
        }

        EventBus.getDefault().register(this);
        return rootView;
    }

    @Subcriber(tag = CLICK_TAG)
    private void updateClickUserName(Person clickPerson) {
        mUserNameTv.setText(clickPerson.name);
    }

    /*
     * 模拟从异步线程发来的更新信息
     */
    @Subcriber
    private void updateTime(String name) {
        Log.e(getTag(), "### update time, thread =  " + Thread.currentThread().getName());

        // 从哪个线程投递来的消息
        mTimerTv.setText("from " + name);

        // post 给TimerThread线程
        EventBus.getDefault().post("I am tom, ", "sayhello");
    }

    @Subcriber(mode = ThreadMode.POST)
    private void invokeInPostThread(String event) {
        Log.e(getTag(), "### invokeInPostThread invoke in thread =  "
                + Thread.currentThread().getName());
    }

    @Override
    public void onDestroy() {
        for (PostThread timerThread : threads) {
            timerThread.interrupt();
        }

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * @author mrsimple
     */
    class PostThread extends Thread {

        int mIndex;

        public PostThread(int index) {
            mIndex = index;
            setName("TimerThread - " + index);
            EventBus.getDefault().register(this);
        }

        /**
         * @param name
         */
        @Subcriber(tag = "sayhello")
        private void hello(String name) {
            Log.d(getTag(), "### hello, " + name + " -->  in " + getName());
        }

        @Override
        public void run() {
            Log.e(getTag(), "### queue : " + EventBus.getDefault().getEventQueue().hashCode()
                    + ", bus = " + EventBus.getDefault());

            while (!this.isInterrupted()) {
                EventBus.getDefault().post(getName());
                try {
                    Thread.sleep(500 * mIndex + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            EventBus.getDefault().unregister(this);
        }
    }
}

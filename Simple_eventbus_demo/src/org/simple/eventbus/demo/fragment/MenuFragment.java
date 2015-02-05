/*
 * Copyright (C) 2015 Mr.Simple <bboyfeiyu@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.simple.eventbus.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subcriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.demo.R;
import org.simple.eventbus.demo.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author mrsimple
 */
public class MenuFragment extends BaseFragment {

    public static final String CLICK_TAG = "click_user";
    /**
     * 
     */
    PostThread[] threads = new PostThread[4];
    /**
     * 显示被点击的用户名的TextView
     */
    TextView mUserNameTv;
    /**
     * Thread name TextView
     */
    TextView mThreadTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);

        mUserNameTv = (TextView) rootView.findViewById(R.id.click_tv);
        mThreadTv = (TextView) rootView.findViewById(R.id.timer_tv);

        // 发布事件
        rootView.findViewById(R.id.my_post_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new User("Mr.Simple" + new Random().nextInt(100)));
            }
        });

        // 发布移除事件的按钮
        rootView.findViewById(R.id.my_remove_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 移除用户
                        EventBus.getDefault().post(new User("User - 1"),
                                "remove");

                        List<User> users = new ArrayList<User>();
                        for (int i = 0; i < 5; i++) {
                            users.add(new User("user - " + i));
                        }

                        EventBus.getDefault().post(users, "list");
                    }
                });

        // 发布异步事件的按钮
        rootView.findViewById(R.id.my_post_async_event_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 将目标函数执行在异步线程中
                        EventBus.getDefault().post(new User("mr.simple-3"), "async");
                    }
                });

        startThread();

        EventBus.getDefault().register(this);
        return rootView;
    }

    private void startThread() {
        for (int i = 0; i < 4; i++) {
            threads[i] = new PostThread(i);
            threads[i].start();
        }
    }

    @Subcriber(tag = CLICK_TAG)
    private void updateClickUserName(User clickPerson) {
        mUserNameTv.setText(clickPerson.name);
    }

    @Subcriber(tag = "list")
    private void subcribeList(List<User> users) {
        Toast.makeText(getActivity(), "list", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < users.size(); i++) {
            Log.e(getTag(), "### user name = " + users.get(i));
        }
    }

    /*
     * 模拟从异步线程发来的更新信息
     */
    @Subcriber
    private void updateTime(String name) {
        Log.e(getTag(), "### update time, thread =  " + Thread.currentThread().getName());

        // 从哪个线程投递来的消息
        mThreadTv.setText("from " + name);

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
            setName("Thread - " + index);

            EventBus.getDefault().register(this);
        }

        /**
         * receiver msg from other thread
         * 
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

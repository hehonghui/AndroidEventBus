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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.demo.R;
import org.simple.eventbus.demo.StickyActivity;
import org.simple.eventbus.demo.bean.StickyUser;
import org.simple.eventbus.demo.bean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 该类是演示AndroidEventBus使用的菜单Fragment,演示不了不同参数类型、不同线程模型的事件发布、接收示例.
 * 一个事件类型的决定因素只有事件的参数类型( Class ) 和 注册时的tag ( 字符串 ) ,线程模型( mode
 * )不会影响事件类型的定位,只会影响订阅函数执行在哪个线程中.
 * <p>
 * 不同组件 (Activity、Fragment、Service等)、不同线程之间都可以通过事件总线来发布事件,它是线程安全的。
 * 只要发布的事件的参数类型和tag都匹配即可接收到事件.
 * <p>
 * 注意 : 如果发布的事件的参数类型是订阅的事件参数的子类,订阅函数默认也会被执行。例如你在订阅函数中订阅的是List<String>类型的事件,
 * 但是在发布时发布的是ArrayList<String>的事件,
 * 因此List<String>是一个泛型抽象,而ArrayList<String>才是具体的实现
 * ,因此这种情况下订阅函数也会被执行。如果你需要订阅函数能够接收到的事件类型必须严格匹配 , 然后通过事件总线{@see
 * EventBus#setMatchPolicy(org.simple.eventbus.matchpolicy.MatchPolicy)}设置匹配策略.
 * 
 * @author mrsimple
 */
public class MenuFragment extends BaseFragment {

    static final String CLICK_TAG = "click_user";
    static final String THREAD_TAG = "sub_thread";
    public static final String ASYNC_TAG = "async";
    public static final String REMOVE_TAG = "remove";

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
                        EventBus.getDefault().post(new User("User - 1"), REMOVE_TAG);
                    }
                });

        // 发布异步事件的按钮
        rootView.findViewById(R.id.my_post_async_event_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 将目标函数执行在异步线程中
                        EventBus.getDefault().post(new User("async-user"), ASYNC_TAG);
                    }
                });

        // 发布事件,传递的是List数据
        rootView.findViewById(R.id.my_post_list_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                postListData();
            }
        });

        // 发布事件,调用的是父类中的函数
        rootView.findViewById(R.id.my_post_to_supper_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                postEventToSuper();
            }
        });

        // 发布事件,将事件投递到子线程中
        rootView.findViewById(R.id.my_post_to_thread_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // post 给PostThread线程
                EventBus.getDefault().post("I am MainThread", THREAD_TAG);
            }
        });

        // 发布事件,事件类型为原始类型,比如int, boolean, float等
        rootView.findViewById(R.id.post_primitive_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(12345);
                // 整型数组
                EventBus.getDefault().post(new int[] {
                        12, 24
                });
                EventBus.getDefault().post(true);
            }
        });

        startThreads();

        EventBus.getDefault().register(this);

        rootView.findViewById(R.id.post_sticky_tv).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 发布Sticky事件
                EventBus.getDefault().postSticky(new StickyUser("我来自Sticky事件 - StickyUser类"));

                // 跳转页面
                Intent intent = new Intent(getActivity(), StickyActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * start post threads
     */
    private void startThreads() {
        for (int i = 0; i < 4; i++) {
            threads[i] = new PostThread(i);
            threads[i].start();
        }
    }

    /**
     * 发布参数是List<T>类型的事件, 接收函数为@{@see #subcribeList(List)}
     */
    private void postListData() {
        List<User> userLisr = new ArrayList<User>();
        for (int i = 0; i < 5; i++) {
            userLisr.add(new User("user - " + i));
        }

        EventBus.getDefault().post(userLisr);
    }

    @Subscriber
    private void subcribeList(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            Log.e(getTag(), "### user name = " + users.get(i));
        }
    }

    /**
     * 订阅点击事件,该事件是从{@see ConstactFragment}中的ListView点击某个项时发布的
     * 
     * @param clickPerson
     */
    @Subscriber(tag = CLICK_TAG)
    private void updateClickUserName(User clickPerson) {
        mUserNameTv.setText(clickPerson.name);
    }

    /**
     * 模拟从异步线程发来的更新信息, {@link PostThread#run()}
     */
    @Subscriber
    private void receiveFrom(String name) {
        // 从哪个线程投递来的消息
        mThreadTv.setText("from " + name);
    }

    /**
     * 接受参数类型为String的事件,执行在发布事件的线程. {@link PostThread#run()}
     * 
     * @param event
     */
    @Subscriber(mode = ThreadMode.POST)
    private void invokeInPostThread(String event) {
        Log.e(getTag(), "### invokeInPostThread invoke in thread =  "
                + Thread.currentThread().getName());
    }

    /**
     * 发布一个消息,接收函数在BaseFragment中
     */
    private void postEventToSuper() {
        EventBus.getDefault().post(new User("super"), SUPER_TAG);
    }

    /**
     * 投递线程,不断地给主线程投递消息,同时也接受主线程投递过来的消息
     * 
     * @author mrsimple
     */
    class PostThread extends Thread {

        int mIndex;

        public PostThread(int index) {
            mIndex = index;
            setName("Thread - " + index);
            // 线程本身也注册到事件总线中,接收从主线程发布的事件
            EventBus.getDefault().register(this);
        }

        /**
         * receiver msg from other thread
         * 
         * @param name
         */
        @Subscriber(tag = THREAD_TAG)
        private void sayHello(String name) {
            Log.d(getTag(), "### hello, " + name + " -->  in " + getName());
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                // 从子线程发布消息
                EventBus.getDefault().post(getName());

                try {
                    Thread.sleep(1000 * mIndex + 3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

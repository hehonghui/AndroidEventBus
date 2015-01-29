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

package org.simple.eventbus;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 启动一个后台线程来发布消息,在死循环中发布消息,默认将接收方法执行在UI线程,如果需要接收方法执行在异步线程,那么则直接使用再开启一个执行线程。
 * 使用享元模式复用Event对象，类似于Handler中的Message.
 * 
 * @author mrsimple
 */
public final class EventBus {

    /**
     * 
     */
    private static final String DESCRIPTOR = EventBus.class.getSimpleName();

    /**
     * key为事件类型,value为所有订阅者
     */
    private final Map<Event, CopyOnWriteArrayList<Subscription>> mSubcriberMap = new HashMap<Event, CopyOnWriteArrayList<Subscription>>();

    /**
     * 事件队列
     */
    ThreadLocal<Queue<Event>> localEvents = new ThreadLocal<Queue<Event>>() {
        protected java.util.Queue<Event> initialValue() {
            return new ConcurrentLinkedQueue<Event>();
        };
    };

    /**
     * 默认的事件总线
     */
    private static EventBus sEventBusDefault;

    /**
     * 事件总线描述符描述符
     */
    private String mDesc = DESCRIPTOR;

    /**
     * @return
     */
    public static EventBus getDefault() {
        if (sEventBusDefault == null) {
            synchronized (EventBus.class) {
                if (sEventBusDefault == null) {
                    sEventBusDefault = new EventBus();
                }
            }
        }
        return sEventBusDefault;
    }

    /**
     * 
     */
    public EventBus() {
        this(DESCRIPTOR);
    }

    /**
     * @param desc
     */
    public EventBus(String desc) {
        mDesc = desc;
    }

    /**
     * @param subscriber
     */
    public void register(Object subscriber) {
        final Method[] allMethods = subscriber.getClass().getMethods();
        for (int i = 0; i < allMethods.length; i++) {
            Method method = allMethods[i];
            // 根据注解来解析函数
            Subcriber annotation = method.getAnnotation(Subcriber.class);
            if (annotation != null) {
                // 获取方法参数
                Class<?>[] paramsTypeClass = method.getParameterTypes();
                // 只能有一个参数
                if (paramsTypeClass != null && paramsTypeClass.length == 1) {
                    Event event = new Event(paramsTypeClass[0], annotation.tag());
                    SubscribeMethod subscribeMethod = new SubscribeMethod(method,
                            paramsTypeClass[0], annotation.mode());
                    // 订阅事件
                    subscibe(event, subscribeMethod, subscriber);
                }
            }
        } // end for
    }

    /**
     * @param event
     * @param method
     * @param subscriber
     */
    private void subscibe(Event event, SubscribeMethod method, Object subscriber) {
        CopyOnWriteArrayList<Subscription> subscriptionLists = mSubcriberMap
                .get(event);
        if (subscriptionLists == null) {
            subscriptionLists = new CopyOnWriteArrayList<Subscription>();
        }

        subscriptionLists.add(new Subscription(subscriber, method));

        Log.d(getDescriptor(), "### 订阅事件 : " + subscriber.getClass().getName() + ", tag = "
                + event.tag + ", event type = " + event.paramClass.getName());
        // 订阅事件
        mSubcriberMap.put(event, subscriptionLists);
    }

    /**
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        Iterator<CopyOnWriteArrayList<Subscription>> iterator = mSubcriberMap
                .values().iterator();
        while (iterator.hasNext()) {
            CopyOnWriteArrayList<Subscription> subscriptions = iterator.next();
            if (subscriptions != null) {
                //
                List<Subscription> foundSubscriptions = new LinkedList<Subscription>();
                Iterator<Subscription> subIterator = subscriptions.iterator();
                while (subIterator.hasNext()) {
                    Subscription subscription = subIterator.next();
                    if (subscription.subscriber.equals(subscriber)) {
                        Log.d(getDescriptor(), "### 移除订阅 " + subscriber.getClass().getName());
                        foundSubscriptions.add(subscription);
                    }
                }

                subscriptions.removeAll(foundSubscriptions);
            }

            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.size() == 0) {
                iterator.remove();
            }
        }

        Log.d(getDescriptor(), "### 订阅size = " + mSubcriberMap.size());
    }

    /**
     * 发布事件,那么则需要找到对应事件类型的所有订阅者
     * 
     * @param event
     */
    public void post(Object event) {
        postWithTag(Event.DEFAULT_TAG, event);
    }

    /**
     * 发布事件
     * 
     * @param tag 事件的tag, 类似于BroadcastReceiver的action
     * @param event 要发布的事件
     */
    public void postWithTag(String tag, Object event) {
        localEvents.get().offer(new Event(event.getClass(), tag));
        dispatchEvents(event);
    }

    /**
     * @param event
     */
    private void dispatchEvents(Object event) {
        Queue<Event> eventsQueue = localEvents.get();
        while (eventsQueue.size() > 0) {
            invoke(eventsQueue.poll(), event);
        }
    }

    /**
     * 处理事件
     * 
     * @param holder
     * @param event
     */
    private void invoke(Event holder, Object event) {
        CopyOnWriteArrayList<Subscription> subscriptions = mSubcriberMap.get(holder);
        for (Subscription subscription : subscriptions) {
            final ThreadMode mode = subscription.subscribeMethod.threadMode;
            // 异步执行目标函数
            if (mode == ThreadMode.ASYNC) {
                AsyncExecutor.sInstance.invokeAsync(subscription, event);
            } else {
                subscription.invoke(event);
            }
        }
    }

    /**
     * @return
     */
    public String getDescriptor() {
        return mDesc;
    }

}

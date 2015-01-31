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

import org.simple.eventbus.handler.AsyncEventHandler;
import org.simple.eventbus.handler.DefaultEventHandler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
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
    private final Map<EventType, CopyOnWriteArrayList<Subscription>> mSubcriberMap = new HashMap<EventType, CopyOnWriteArrayList<Subscription>>();

    /**
     * 事件队列
     */
    ThreadLocal<Queue<EventType>> localEvents = new ThreadLocal<Queue<EventType>>() {
        protected java.util.Queue<EventType> initialValue() {
            return new ConcurrentLinkedQueue<EventType>();
        };
    };

    /**
     * 
     */
    DefaultEventHandler mMainEventHandler = new DefaultEventHandler();

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
        if (subscriber == null) {
            return;
        }
        final Method[] allMethods = subscriber.getClass().getDeclaredMethods();
        for (int i = 0; i < allMethods.length; i++) {
            Method method = allMethods[i];
            // 根据注解来解析函数
            Subcriber annotation = method.getAnnotation(Subcriber.class);
            if (annotation != null) {
                // 获取方法参数
                Class<?>[] paramsTypeClass = method.getParameterTypes();
                // 只能有一个参数
                if (paramsTypeClass != null && paramsTypeClass.length == 1) {
                    EventType event = new EventType(paramsTypeClass[0], annotation.tag());
                    TargetMethod subscribeMethod = new TargetMethod(method,
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
    private void subscibe(EventType event, TargetMethod method, Object subscriber) {
        CopyOnWriteArrayList<Subscription> subscriptionLists = mSubcriberMap
                .get(event);
        if (subscriptionLists == null) {
            subscriptionLists = new CopyOnWriteArrayList<Subscription>();
        }

        Subscription newSubscription = new Subscription(subscriber, method);
        if (subscriptionLists.contains(newSubscription)) {
            return;
        }

        subscriptionLists.add(newSubscription);

        Log.d(getDescriptor(), "### 订阅事件 : " + subscriber.getClass().getName() + ", tag = "
                + event.tag + ", event type = " + event.paramClass.getName());
        // 订阅事件
        mSubcriberMap.put(event, subscriptionLists);
    }

    /**
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        if (subscriber == null) {
            return;
        }

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

                // 移除该subscriber的相关的Subscription
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
        post(event, EventType.DEFAULT_TAG);
    }

    /**
     * 发布事件
     * 
     * @param event 要发布的事件
     * @param tag 事件的tag, 类似于BroadcastReceiver的action
     */
    public void post(Object event, String tag) {
        localEvents.get().offer(new EventType(event.getClass(), tag));
        dispatchEvents(event);
    }

    /**
     * @param event
     */
    private void dispatchEvents(Object event) {
        Queue<EventType> eventsQueue = localEvents.get();
        while (eventsQueue.size() > 0) {
            handleEvent(eventsQueue.poll(), event);
        }
    }

    /**
     * 处理事件
     *
     * @param holder
     * @param event
     */
    private void handleEvent(EventType eventType, Object event) {
        List<Subscription> subscriptions = mSubcriberMap.get(eventType);
        if (subscriptions == null) {
            return;
        }

        for (Subscription subscription : subscriptions) {
            final ThreadMode mode = subscription.threadMode;
            // 异步执行目标函数
            if (mode == ThreadMode.ASYNC) {
                AsyncEventHandler.getInstance().handleEvent(subscription, event);
            } else {
                mMainEventHandler.handleEvent(subscription, event);
            }
        }
    }

    /**
     * 获取
     * 
     * @param event
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<Subscription> getSubscriptions(EventType event) {
        List<Subscription> result = mSubcriberMap.get(event);
        return result != null ? result : Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    public Collection<EventType> getEvents() {
        Collection<EventType> result = mSubcriberMap.keySet();
        return result != null ? result : Collections.EMPTY_LIST;
    }

    /**
     * @return
     */
    public String getDescriptor() {
        return mDesc;
    }

}

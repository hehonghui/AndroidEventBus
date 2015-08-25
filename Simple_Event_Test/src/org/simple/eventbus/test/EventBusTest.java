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

package org.simple.eventbus.test;

import android.test.AndroidTestCase;
import android.util.Log;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.EventType;
import org.simple.eventbus.Subscription;
import org.simple.eventbus.test.mock.MockSubcriber;
import org.simple.eventbus.test.mock.SingleSubscriber;
import org.simple.eventbus.test.mock.StickySubscriber;
import org.simple.eventbus.test.mock.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author mrsimple
 */
public class EventBusTest extends AndroidTestCase {

    EventBus bus = EventBus.getDefault();

    Map<EventType, CopyOnWriteArrayList<Subscription>> mSubcriberMap = bus.getSubscriberMap();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        EventBus.getDefault().clear();
    }

    /**
     * 获取某个事件的观察者列表
     * 
     * @param event 事件类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<Subscription> getSubscriptions(EventType event) {
        List<Subscription> result = mSubcriberMap.get(event);
        return result != null ? result : Collections.EMPTY_LIST;
    }

    /**
     * 获取已经注册的事件类型列表
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<EventType> getEventTypes() {
        Collection<EventType> result = mSubcriberMap.keySet();
        return result != null ? result : Collections.EMPTY_LIST;
    }

    /**
     * 
     */
    public void testRepeatRegister() {
        MockSubcriber mockActivity = new MockSubcriber();
        for (int i = 0; i < 10; i++) {
            // 测试重复注册一个对象
            bus.register(mockActivity);
        }

        // 类型为Person的有效注册函数为2个.
        assertEquals(2, getSubscriptions(new EventType(User.class)).size());
        // Object类型的函数为1一个
        assertEquals(1, getSubscriptions(new EventType(Object.class)).size());
        // NULL类型的函数为2一个
        assertEquals(2, getSubscriptions(new EventType(null)).size());
        // NULL类型tag为tag_null的函数为1一个
        assertEquals(1, getSubscriptions(new EventType(null, "tag_null")).size());
    }

    /**
     * 
     */
    public void testRepeatRegisterWithTag() {
        MockSubcriber mockActivity = new MockSubcriber();
        for (int i = 0; i < 10; i++) {
            // 测试重复注册一个对象
            bus.register(mockActivity);
        }

        // 类型为Person且tag为"test"的有效注册函数为1个.
        assertEquals(1, getSubscriptions(new EventType(User.class, "test")).size());

        // 类型为Person且tag为"another"的有效注册函数为1个.
        assertEquals(1, getSubscriptions(new EventType(User.class, "another")).size());

        // 类型为NULL且tag为"tag_null"的有效注册函数为1个.
        assertEquals(1, getSubscriptions(new EventType(null, "tag_null")).size());
    }

    /**
     * 
     */
    public void testSubscribeAndPost() {
        MockSubcriber mockActivity = new MockSubcriber();
        // 正常注册与发布
        bus.register(mockActivity);
        bus.post(new User("mr.simple"));
        bus.post(User.class);
        bus.post("tag_null");

        // 移除对象
        bus.unregister(mockActivity);
        // 移除对象之后post不会出现问题
        bus.post(new User("mr.simple"));
        // 移除对象测试
        assertEquals(0, getSubscriptions(new EventType(User.class)).size());
        assertEquals(0, getSubscriptions(new EventType(Object.class)).size());
    }

    public void testRegisterNull() {
        bus.register(null);
    }

    public void testUnRegisterNull() {
        bus.unregister(null);
    }

    public void testStickyEvents() {
        assertEquals(0, bus.getStickyEvents().size());
        bus.postSticky("hello", EventType.DEFAULT_TAG);
        assertEquals(1, bus.getStickyEvents().size());
        final String tag = "sticky";
        bus.postSticky("world", tag);
        assertEquals(2, bus.getStickyEvents().size());
        
        bus.postSticky(String.class);
        assertEquals(3, bus.getStickyEvents().size());
        bus.postSticky(tag);
        assertEquals(4, bus.getStickyEvents().size());
        bus.postSticky(tag, String.class);
        assertEquals(5, bus.getStickyEvents().size());

        bus.removeStickyEvent(String.class);
        assertEquals(3, bus.getStickyEvents().size());

        bus.removeStickyEvent(String.class, tag);
        assertEquals(1, bus.getStickyEvents().size());

        bus.removeStickyEvent(null, tag);
        assertEquals(0, bus.getStickyEvents().size());
    }

    public void testStickySubscriber() {
        StickySubscriber subscriber = new StickySubscriber();
        assertEquals(null, subscriber.mStickyName);
        assertEquals(null, subscriber.mStickyTag);
        // 未注册时发布事件,那么对象接收不到事件
        bus.post("hello", EventType.DEFAULT_TAG);
        // 普通注册形式
        bus.register(subscriber);
        assertEquals(null, subscriber.mStickyName);
        assertEquals(null, subscriber.mStickyTag);
        bus.unregister(subscriber);

        // post sticky事件
        bus.postSticky("simple", EventType.DEFAULT_TAG);
        // 以sticky的形式注册
        bus.registerSticky(subscriber);

        // 接收到数据
        assertEquals("simple", subscriber.mStickyName);
        assertEquals(null, subscriber.mStickyTag);

        // post sticky事件, 含有tag, 这里需要注意
        bus.postSticky("simple", "sticky");
        assertEquals("sticky", subscriber.mStickyTag);

    }

    /**
     * 
     */
    public void testPerformence() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            SingleSubscriber subscriber = new SingleSubscriber();
            bus.register(subscriber);
        }
        long end = System.nanoTime();
        Log.d(getName(), "### register 1000 subscriber, time = " + (end - start) + " ns, "
                + (end - start) / 1 * 1e6 + " ms");

        assertEquals(1000, getSubscriptions(new EventType(Object.class)).size());
    }

}

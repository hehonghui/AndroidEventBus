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
import org.simple.eventbus.test.mock.MockSubcriber;
import org.simple.eventbus.test.mock.SingleSubscriber;
import org.simple.eventbus.test.mock.User;

/**
 * @author mrsimple
 */
public class EventBusTest extends AndroidTestCase {

    EventBus bus = EventBus.getDefault();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        EventBus.getDefault().clear();
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
        assertEquals(2, bus.getSubscriptions(new EventType(User.class)).size());
        // Object类型的函数为1一个
        assertEquals(1, bus.getSubscriptions(new EventType(Object.class)).size());
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
        assertEquals(1, bus.getSubscriptions(new EventType(User.class, "test")).size());

        // 类型为Person且tag为"another"的有效注册函数为1个.
        assertEquals(1, bus.getSubscriptions(new EventType(User.class, "another")).size());
    }

    /**
     * 
     */
    public void testSubscribeAndPost() {
        MockSubcriber mockActivity = new MockSubcriber();
        // 正常注册与发布
        bus.register(mockActivity);
        bus.post(new User("mr.simple"));

        // 移除对象
        bus.unregister(mockActivity);
        // 移除对象之后post不会出现问题
        bus.post(new User("mr.simple"));
        // 移除对象测试
        assertEquals(0, bus.getSubscriptions(new EventType(User.class)).size());
        assertEquals(0, bus.getSubscriptions(new EventType(Object.class)).size());
    }

    public void testRegisterNull() {
        bus.register(null);
    }

    public void testUnRegisterNull() {
        bus.unregister(null);
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

        assertEquals(1000, bus.getSubscriptions(new EventType(Object.class)).size());
    }

}

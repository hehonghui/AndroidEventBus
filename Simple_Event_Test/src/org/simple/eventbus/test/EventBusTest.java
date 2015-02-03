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

package org.simple.eventbus.test;

import android.test.AndroidTestCase;
import android.util.Log;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.EventType;
import org.simple.eventbus.test.mock.MockSubcriber;
import org.simple.eventbus.test.mock.Person;
import org.simple.eventbus.test.mock.SingleSubscriber;

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
        assertEquals(2, bus.getSubscriptions(new EventType(Person.class)).size());
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
        assertEquals(1, bus.getSubscriptions(new EventType(Person.class, "test")).size());

        // 类型为Person且tag为"another"的有效注册函数为1个.
        assertEquals(1, bus.getSubscriptions(new EventType(Person.class, "another")).size());
    }

    /**
     * 
     */
    public void testSubscribeAndPost() {
        MockSubcriber mockActivity = new MockSubcriber();
        // 正常注册与发布
        bus.register(mockActivity);
        bus.post(new Person("mr.simple"));

        // 移除对象
        bus.unregister(mockActivity);
        // 移除对象之后post不会出现问题
        bus.post(new Person("mr.simple"));
        // 移除对象测试
        assertEquals(0, bus.getSubscriptions(new EventType(Person.class)).size());
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

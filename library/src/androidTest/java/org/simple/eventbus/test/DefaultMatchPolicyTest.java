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

import static junit.framework.TestCase.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.EventType;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.matchpolicy.DefaultMatchPolicy;
import org.simple.eventbus.matchpolicy.MatchPolicy;
import org.simple.eventbus.test.mock.User;

import java.util.List;

/**
 * 注意 : 在发布事件时会查找与该事件匹配的订阅函数,默认的查找器会构造该事件父类的EventType,发布一个事件可能会引起多个事件。
 * 
 * @author mrsimple
 */
@RunWith(AndroidJUnit4.class)
public class DefaultMatchPolicyTest {

    @Before
    public void setUp() throws Exception {
        EventBus.getDefault().register(this);
    }

    @After
    public void tearDown() throws Exception {
        EventBus.getDefault().unregister(this);
    }

    @Subscriber
    private void whatEver(User user) {

    }

    @Subscriber
    private void singleObjectParam(Object obj) {

    }

    @Test
    public void testFindMatchMethod() {
        MatchPolicy policy = new DefaultMatchPolicy();
        List<EventType> types = policy.findMatchEventTypes(new EventType(User.class,
                EventType.DEFAULT_TAG), new User("org/simple/eventbus/test"));
        assertEquals(2, types.size());

        types.clear();

        // 发布一个Object事件
        types = policy.findMatchEventTypes(new EventType(Object.class,
                EventType.DEFAULT_TAG), new Object());
        assertEquals(1, types.size());
    }
}

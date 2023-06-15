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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.EventType;
import org.simple.eventbus.SubsciberMethodHunter;
import org.simple.eventbus.Subscription;
import org.simple.eventbus.test.mock.PrimitiveParamObject;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SubscriberMethodHunterTest  {

    SubsciberMethodHunter mHunter = new SubsciberMethodHunter(EventBus.getDefault()
            .getSubscriberMap());

    PrimitiveParamObject object = new PrimitiveParamObject();

    @Before
    public void setUp() throws Exception {
        EventBus.getDefault().register(object);
    }

    @After
    public void tearDown() throws Exception {
        EventBus.getDefault().unregister(object);
    }

    @Test
    public void testFindPrimitiveParamMethod() {
        mHunter.findSubcribeMethods(object);
        List<Subscription> subscriptions = EventBus.getDefault().getSubscriberMap()
                .get(new EventType(Integer.class));
        assertEquals(1, subscriptions.size());
        assertEquals(object, subscriptions.get(0).subscriber.get());
    }

}

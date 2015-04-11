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
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.handler.AsyncEventHandler;
import org.simple.eventbus.test.mock.User;

public class ThreadModeTest extends AndroidTestCase {

    private static final String MAIN_TAG = "main";
    private static final String POST_TAG = "post";
    private static final String ASYNC_TAG = "async";

    Thread uiThread;

    protected void setUp() throws Exception {
        super.setUp();
        uiThread = Thread.currentThread();
        EventBus.getDefault().register(this);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        EventBus.getDefault().register(this);
        EventBus.getDefault().clear();
    }

    public void testExecuteMainThread() {
        EventBus.getDefault().post(new User("main-thread"), MAIN_TAG);

        waitDispatchEvent();
    }

    @Subscriber(tag = MAIN_TAG)
    private void executeOnUIThread(User user) {
        assertEquals("main-thread", user.name);
        // can not test main thread mode
    }

    public void testExecutePostThread() {
        EventBus.getDefault().post(new User("post-thread"), POST_TAG);

        waitDispatchEvent();
    }

    @Subscriber(tag = POST_TAG, mode = ThreadMode.POST)
    private void executeOnPostThread(User user) {
        assertEquals("post-thread", user.name);
        assertEquals(uiThread, Thread.currentThread());
    }

    public void testExecuteAsyncThread() {
        EventBus.getDefault().post(new User("async-thread"), ASYNC_TAG);
        waitDispatchEvent();
    }

    @Subscriber(tag = ASYNC_TAG, mode = ThreadMode.ASYNC)
    private void executeOnAsyncThread(User user) {
        assertEquals("async-thread", user.name);
        Log.e(getName(), "### executeOnAsyncThread flag");
        assertEquals(AsyncEventHandler.class.getSimpleName(), Thread.currentThread().getName());
    }

    private void waitDispatchEvent() {
        synchronized (this) {
            try {
                wait(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

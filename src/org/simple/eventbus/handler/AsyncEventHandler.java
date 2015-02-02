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

package org.simple.eventbus.handler;

import android.os.Handler;
import android.os.HandlerThread;

import org.simple.eventbus.Subscription;

/**
 * 事件的异步处理,将事件的处理函数执行在子线程中
 * 
 * @author mrsimple
 */
public class AsyncEventHandler implements EventHandler {

    /**
     * 
     */
    DispatcherThread mDispatcherThread;

    /**
     * 事件处理器
     */
    EventHandler mEventHandler = new DefaultEventHandler();
    
    /**
     * @param name
     */
    public AsyncEventHandler() {
        mDispatcherThread = new DispatcherThread(AsyncEventHandler.class.getSimpleName());
        mDispatcherThread.start();
    }

    /**
     * 将订阅的函数执行在异步线程中
     * 
     * @param subscription
     * @param event
     */
    public void handleEvent(final Subscription subscription, final Object event) {
        mDispatcherThread.post(new Runnable() {

            @Override
            public void run() {
                mEventHandler.handleEvent(subscription, event);
            }
        });
    }

    /**
     * @author mrsimple
     */
    class DispatcherThread extends HandlerThread {

        /**
         * 关联了AsyncExecutor消息队列的Handler
         */
        protected Handler mAsyncHandler;

        /**
         * @param name
         */
        public DispatcherThread(String name) {
            super(name);
        }

        /**
         * @param runnable
         */
        public void post(Runnable runnable) {
            if (mAsyncHandler == null) {
                throw new NullPointerException("mAsyncHandler == null, please call start() first.");
            }

            mAsyncHandler.post(runnable);
        }

        @Override
        public synchronized void start() {
            super.start();
            mAsyncHandler = new Handler(this.getLooper());
        }

    }

}

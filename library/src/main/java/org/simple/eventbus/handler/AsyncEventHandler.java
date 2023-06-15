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
     * 事件分发线程
     */
    DispatcherThread mDispatcherThread;

    /**
     * 事件处理器
     */
    EventHandler mEventHandler = new DefaultEventHandler();

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

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
import android.os.Looper;

import org.simple.eventbus.Subscription;

/**
 * 事件处理在UI线程,通过Handler将事件处理post到UI线程的消息队列
 * 
 * @author mrsimple
 */
public class UIThreadEventHandler implements EventHandler {

    /**
     * ui handler
     */
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    /**
     * 
     */
    DefaultEventHandler mEventHandler = new DefaultEventHandler();

    /**
     * @param subscription
     * @param event
     */
    public void handleEvent(final Subscription subscription, final Object event) {
        mUIHandler.post(new Runnable() {

            @Override
            public void run() {
                mEventHandler.handleEvent(subscription, event);
            }
        });
    }

}

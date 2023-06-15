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

import org.simple.eventbus.Subscription;

import java.lang.reflect.InvocationTargetException;

/**
 * 事件在哪个线程post,事件的接收就在哪个线程
 * 
 * @author mrsimple
 */
public class DefaultEventHandler implements EventHandler {
    /**
     * handle the event
     * 
     * @param subscription
     * @param event
     */
    public void handleEvent(Subscription subscription, Object event) {
        if (subscription == null
                || subscription.subscriber.get() == null) {
            return;
        }
        try {
            // 执行
            subscription.targetMethod.invoke(subscription.subscriber.get(), event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

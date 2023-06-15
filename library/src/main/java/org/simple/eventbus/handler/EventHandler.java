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

/**
 * 事件处理接口
 * 
 * @author mrsimple
 */
public interface EventHandler {
    /**
     * 处理事件
     * 
     * @param subscription 订阅对象
     * @param event 待处理的事件
     */
    void handleEvent(Subscription subscription, Object event);
}

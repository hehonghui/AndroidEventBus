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

package org.simple.eventbus;

import java.lang.reflect.Method;

/**
 * 订阅某个事件的函数类,包含了函数信息、参数名、执行的线程模式
 * 
 * @author mrsimple
 */
 class TargetMethod {
    /**
     * 订阅者的目标函数
     */
    public Method method;
    /**
     * 事件类型
     */
//    public Class<?> eventType;
    public EventType eventType ;
    /**
     * 处理事件的线程模式
     */
    public ThreadMode threadMode;

    /**
     * @param md
     * @param eventType
     * @param mode
     */
    public TargetMethod(Method md, EventType type, ThreadMode mode) {
        this.method = md;
        this.method.setAccessible(true);
        this.eventType = type;
        this.threadMode = mode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((method == null) ? 0 : method.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TargetMethod other = (TargetMethod) obj;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        } else if (!eventType.equals(other.eventType))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.getName().equals(other.method.getName()))
            return false;
        return true;
    }

}

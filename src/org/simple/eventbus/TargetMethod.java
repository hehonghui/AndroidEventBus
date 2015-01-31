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
    public Class<?> eventType;
    /**
     * 处理事件的线程模式
     */
    public ThreadMode threadMode;

    /**
     * @param md
     * @param eventType
     * @param mode
     */
    public TargetMethod(Method md, Class<?> eventType, ThreadMode mode) {
        this.method = md;
        this.method.setAccessible(true);
        this.eventType = eventType;
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

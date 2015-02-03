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
 * 订阅者对象,包含订阅者和目标方法
 * 
 * @author mrsimple
 */
public class Subscription {
    /**
     * 
     */
    public Object subscriber;
    /**
     * 
     */
    public Method targetMethod;
    /**
     * 执行事件的线程模型
     */
    public ThreadMode threadMode;

    /**
     * @param subscriber
     * @param method
     */
    public Subscription(Object subscriber, TargetMethod targetMethod) {
        this.subscriber = subscriber;
        this.targetMethod = targetMethod.method;
        this.threadMode = targetMethod.threadMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subscriber == null) ? 0 : subscriber.hashCode());
        result = prime * result + ((targetMethod == null) ? 0 : targetMethod.hashCode());
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
        Subscription other = (Subscription) obj;
        if (subscriber == null) {
            if (other.subscriber != null)
                return false;
        } else if (!subscriber.equals(other.subscriber))
            return false;
        if (targetMethod == null) {
            if (other.targetMethod != null)
                return false;
        } else if (!targetMethod.equals(other.targetMethod))
            return false;
        return true;
    }

}

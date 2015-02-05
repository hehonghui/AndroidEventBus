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

package org.simple.eventbus.config;

import org.simple.eventbus.handler.AsyncEventHandler;
import org.simple.eventbus.handler.DefaultEventHandler;
import org.simple.eventbus.handler.EventHandler;
import org.simple.eventbus.handler.UIThreadEventHandler;
import org.simple.eventbus.matchpolicy.DefaultMatchPolicy;
import org.simple.eventbus.matchpolicy.MatchPolicy;

/**
 * Event Bus Configuration, like builder mode
 * 
 * @author mrsimple
 */
public final class EventBusConfig {

    /**
     * 将接收方法执行在UI线程
     */
    EventHandler mUIThreadEventHandler = new UIThreadEventHandler();

    /**
     * 哪个线程执行的post,接收方法就执行在哪个线程
     */
    EventHandler mPostThreadHandler = new DefaultEventHandler();

    /**
     * 异步线程中执行订阅方法
     */
    EventHandler mAsyncEventHandler = new AsyncEventHandler();
    /**
     * 订阅函数匹配策略类
     */
    MatchPolicy mMatchPolicy = new DefaultMatchPolicy();

    public EventBusConfig setMatchPolicy(MatchPolicy mMatchPolicy) {
        this.mMatchPolicy = mMatchPolicy;
        return this;
    }

    public EventBusConfig setUIThreadEventHandler(EventHandler handler) {
        this.mUIThreadEventHandler = handler;
        return this;
    }

    public EventBusConfig setPostThreadHandler(EventHandler handler) {
        this.mPostThreadHandler = handler;
        return this;
    }

    public EventBusConfig setAsyncEventHandler(EventHandler handler) {
        this.mAsyncEventHandler = handler;
        return this;
    }

    public EventHandler getUIThreadEventHandler() {
        return mUIThreadEventHandler;
    }

    public EventHandler getPostThreadHandler() {
        return mPostThreadHandler;
    }

    public EventHandler getAsyncEventHandler() {
        return mAsyncEventHandler;
    }

    public MatchPolicy getMatchPolicy() {
        return mMatchPolicy;
    }

}

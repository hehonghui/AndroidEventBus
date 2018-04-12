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

package org.simple.eventbus.matchpolicy;

import org.simple.eventbus.EventType;
import org.simple.eventbus.NULL;

import java.util.LinkedList;
import java.util.List;

public class DefaultMatchPolicy implements MatchPolicy {

    @Override
    public List<EventType> findMatchEventTypes(EventType type, Object... aEvent) {
        List<EventType> result = new LinkedList<EventType>();
        if (aEvent == null || aEvent.length == 0) {
            Class<?>[] eventClass = new Class[]{new NULL().getClass()};
            result.add(new EventType(type.tag, eventClass));
        } else {
            Class<?>[] eventClass = new Class<?>[aEvent.length];
            for (int i = 0; i < aEvent.length; i++) {
                Object o = aEvent[i];
                if (o == null)
                    o = new NULL();
                eventClass[i] = o.getClass();
            }
//            while (eventClass != null) {
            result.add(new EventType(type.tag, eventClass));
//                eventClass = addInterfaces(result, type.tag, eventClass);
//            }
        }

        return result;
    }

    /**
     * 获取该对象的所有接口类型
     *
     * @param eventTypes 存储列表
     * @param eventClass 事件实现的所有接口
     */
    private void addInterface(List<EventType> eventTypes, String tag, Class<?> eventClass) {
        if (eventClass == null) {
            return;
        }
        Class<?>[] interfacesClasses = eventClass.getInterfaces();
        for (Class<?> interfaceClass : interfacesClasses) {
            if (!eventTypes.contains(interfaceClass)) {
                eventTypes.add(new EventType(tag, interfaceClass));
                addInterface(eventTypes, tag, interfaceClass);
            }
        }
    }

}

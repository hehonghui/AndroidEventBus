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

package org.simple.eventbus.test.mock;

import org.simple.eventbus.Subscriber;

/**
 * @author mrsimple
 */
public class MockSubcriber {

    @Subscriber
    void onEventNoParam() {
    }

    @Subscriber
    void onEventTwoParam(User person, int id) {

    }

    @Subscriber
    void onEvent(User person) {
        System.out.println("invoke onEvent(Person person) in " + this.getClass().getName());
        System.out.println("person name =  " + person.name);
    }

    /**
     * 参数相同,函数名不同
     * 
     * @param person
     */
    @Subscriber
    void addPerson(User person) {
        System.out.println("invoke addPerson(Person person) in " + this.getClass().getName());
        System.out.println("person name =  " + person.name);
    }

    /**
     * test tag
     * 
     * @param person
     */
    @Subscriber(tag = "org/simple/eventbus/test")
    void methodWithTag(User person) {

    }

    /**
     * another tag
     * 
     * @param person
     */
    @Subscriber(tag = "another")
    void methodWithAnotherTag(User person) {

    }

    /**
     * 同名函数,但是参数不同
     * 
     * @param object
     */
    @Subscriber
    void onEvent(Object object) {
        System.out.println("invoke onEvent(Person person) in " + this.getClass().getName());
    }
}

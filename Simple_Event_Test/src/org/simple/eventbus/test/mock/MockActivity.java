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

package org.simple.eventbus.test.mock;

import org.simple.eventbus.Subcriber;

/**
 * @author mrsimple
 */
public class MockActivity {

    @Subcriber
    void onEventNoParam() {
    }

    @Subcriber
    void onEventTwoParam(Person person, int id) {

    }

    @Subcriber
    void onEvent(Person person) {
        System.out.println("invoke onEvent(Person person) in " + this.getClass().getName());
        System.out.println("person name =  " + person.name);
    }

    /**
     * 参数相同,函数名不同
     * 
     * @param person
     */
    @Subcriber
    void addPerson(Person person) {
        System.out.println("invoke addPerson(Person person) in " + this.getClass().getName());
        System.out.println("person name =  " + person.name);
    }

    /**
     * test tag
     * 
     * @param person
     */
    @Subcriber(tag = "test")
    void methodWithTag(Person person) {

    }

    /**
     * another tag
     * 
     * @param person
     */
    @Subcriber(tag = "another")
    void methodWithAnotherTag(Person person) {

    }

    /**
     * 同名函数,但是参数不同
     * 
     * @param object
     */
    @Subcriber
    void onEvent(Object object) {
        System.out.println("invoke onEvent(Person person) in " + this.getClass().getName());
    }
}

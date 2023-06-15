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

package org.simple.eventbus.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mrsimple
 */
@RunWith(AndroidJUnit4.class)
public class EventTypeTest extends TestCase {

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        EventBus.getDefault().clear();
    }

    /**
     * 检测EventType作为map的key的唯一性
     */
    @Test
    public void testKeysInMap() {
        Map<EventType, String> map = new HashMap<EventType, String>();
        String tag = "default";
        for (int i = 0; i < 10; i++) {
            map.put(new EventType(String.class, tag), tag + i);
        }

        assertEquals(1, map.size());
        assertEquals("default9", map.get(new EventType(String.class, "default")));
    }

    /**
     * 检测类型相同,tag不同的EventType的唯一性
     */
    @Test
    public void testDiffKeysInMap() {
        Map<EventType, String> map = new HashMap<EventType, String>();
        String tag = "default";
        for (int i = 0; i < 10; i++) {
            map.put(new EventType(String.class, tag + i), tag + i);
        }

        assertEquals(10, map.size());
    }

    /**
     * 检测类型相同,tag不同的EventType的唯一性
     */
    @Test
    public void testDiffParamKeysInMap() {
        Map<EventType, String> map = new HashMap<EventType, String>();
        map.put(new EventType(String.class), "String");
        map.put(new EventType(String.class, "my_tag_1"), "String");

        map.put(new EventType(Object.class), "Object");
        map.put(new EventType(Object.class, "my_tag_2"), "Object");

        map.put(new EventType(Boolean.class), "Boolean");
        map.put(new EventType(Boolean.class, "my_tag_3"), "Boolean");

        assertEquals(6, map.size());
    }

    @Test
    public void testEquals() {
        assertEquals(new EventType(String.class, "tag"), new EventType(String.class, "tag"));
        assertFalse( new EventType(String.class, "tag").equals(new EventType(String.class, "s_tag")));
    }
}

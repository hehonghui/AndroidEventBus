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

/**
 * <p>
 * 该类是描述一个函数唯一性的对象，参数类型、tag两个条件保证了对象的唯一性.通过该类的对象来查找注册了相应类型和tag的所有订阅者
 * {@see Subscription}, 并且在接到消息时调用所有订阅者对应的函数.
 * 
 * @author mrsimple
 */
public final class EventType {
	/**
	 * 默认的tag
	 */
	public static final String DEFAULT_TAG = "default_tag";

	/**
	 * 参数类型
	 */
	Class[]<?> paramClass;
	/**
	 * 函数的tag
	 */
	public String tag = DEFAULT_TAG;

	/**
	 * @param aClass
	 */
	public EventType(Class<?> aClass) {
		this(aClass, DEFAULT_TAG);
	}

	Object event;

	public EventType(Class[]<?> aClass, String aTag) {
		paramClass = aClass;
		tag = aTag;
	}

	@Override
	public final String toString() {
		return "EventType [paramClass=" + (null != paramClass ? paramClass.getName() : "EmptyParams") + ", tag=" + tag + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paramClass == null) ? 0 : paramClass.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventType other = (EventType) obj;
		if (paramClass == null) {
			if (other.paramClass != null)
				return false;
		} else if (!paramClass.equals(other.paramClass))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public final boolean equals(Class<?> cls, String tag) {
		if (paramClass == null) {
			if (cls != null)
				return false;
		} else if (!paramClass.equals(cls))
			return false;
		if (this.tag == null) {
			if (tag != null)
				return false;
		} else if (!this.tag.equals(tag))
			return false;
		return true;
	}

	public final String getParamName() {
		return null != paramClass ? paramClass.getSimpleName() : "EmptyParam";
	}

	public final boolean isAssignableFromParam(EventType foundEventType) {
		return null != paramClass ? paramClass.isAssignableFrom(foundEventType.paramClass) : paramClass == foundEventType.paramClass;
	}

}

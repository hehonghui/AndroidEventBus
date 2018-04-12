package org.simple.eventbus;

/**
 * Created by xujian on 15/11/18.
 */
public class NULL {
    private Object value = null;

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass());
    }
}

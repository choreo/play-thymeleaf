/*
 * Copyright 2012 Satoshi Takata Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package play.modules.thymeleaf.adapter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import play.mvc.Scope.Session;

/**
 * This class adapts Session instance as a Map.
 */

public class SessionAdapter implements Map<String, String> {
    private Session session = Session.current();

    private Map<String, String> dataMap;

    @Override
    public void clear() {
        session.clear();

    }

    @Override
    public boolean containsKey(Object key) {
        return session.contains((String) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getDataMap().containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return getDataMap().entrySet();
    }

    @Override
    public String get(Object key) {
        return session.get((String) key);
    }

    @Override
    public boolean isEmpty() {
        return getDataMap().isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return getDataMap().keySet();
    }

    @Override
    public String put(String key, String value) {
        String prev = session.get(key);
        session.put(key, value);
        return prev;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        Set<?> set = m.entrySet();
        for (Object object : set) {
            Entry<String, String> entry = (Entry<String, String>) object;
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String remove(Object key) {
        String prev = session.get((String) key);
        session.remove((String) key);
        return prev;
    }

    @Override
    public int size() {
        return getDataMap().size();
    }

    @Override
    public Collection<String> values() {
        return getDataMap().values();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getDataMap() {
        if (this.dataMap == null) {
            Field mapField;
            try {
                mapField = session.getClass()
                                  .getDeclaredField("data");
                mapField.setAccessible(true);
                dataMap = (Map<String, String>) mapField.get(session);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this.dataMap;
    }

}

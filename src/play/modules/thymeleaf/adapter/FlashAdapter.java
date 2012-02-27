package play.modules.thymeleaf.adapter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import play.mvc.Scope.Flash;

/**
 * This class adapts Flash instance as a Map.
 */

public class FlashAdapter implements Map<String, String> {
    private Flash flash = Flash.current();

    private Map<String, String> dataMap;

    @Override
    public void clear() {
        flash.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return flash.contains((String) key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getDataMap().containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return getDataMap().entrySet();
    }

    @Override
    public String get(Object key) {
        return flash.get((String) key);
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
        String prev = flash.get(key);
        flash.put(key, value);
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
        String prev = flash.get((String) key);
        flash.remove((String) key);
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
                mapField = flash.getClass()
                                .getDeclaredField("data");
                mapField.setAccessible(true);
                dataMap = (Map<String, String>) mapField.get(flash);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this.dataMap;
    }

}

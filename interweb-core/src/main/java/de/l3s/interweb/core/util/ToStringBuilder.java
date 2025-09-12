package de.l3s.interweb.core.util;

import java.util.Collection;

public class ToStringBuilder {
    private final StringBuilder sb;
    private final boolean appendEmpty;
    private boolean hasFields = false;

    public ToStringBuilder(Object object) {
        this(object, false);
    }

    public ToStringBuilder(Object object, boolean appendEmpty) {
        sb = new StringBuilder();
        sb.append(object.getClass().getSimpleName()).append(" {");
        this.appendEmpty = appendEmpty;
    }

    public ToStringBuilder append(String name, Object value) {
        if (!appendEmpty && isEmpty(value)) {
            return this;
        }

        if (hasFields) {
            sb.append(", ");
        }
        sb.append(name).append('=');
        if (value == null) {
            sb.append("null");
        } else if (value.getClass().isArray()) {
            appendArray(value);
        } else {
            sb.append(value);
        }
        hasFields = true;
        return this;
    }

    private boolean isEmpty(Object value) {
        return switch (value) {
            case null -> true;
            case String str -> str.isEmpty();
            case Collection<?> col -> col.isEmpty();
            default -> false;
        };
    }

    private void appendArray(Object array) {
        sb.append('[');
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(", ");
            Object element = java.lang.reflect.Array.get(array, i);
            sb.append(element);
        }
        sb.append(']');
    }

    public String build() {
        sb.append('}');
        return sb.toString();
    }
}


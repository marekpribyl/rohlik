package com.assignment.rohlik.support;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionUtil {

    /**
     * Null safe empty check
     */
    public static boolean nullSafeIsEmpty(Collection src) {
        return src == null || src.isEmpty();
    }

    /**
     * Null safe check whether collection is not empty
     */
    public static boolean nullSafeIsNotEmpty(Collection<?> src) {
        return !nullSafeIsEmpty(src);
    }

    /**
     * Returns stream from collection, if the collection is null returns empty stream
     */
    public static <T> Stream<T> nullSafeStream(Collection<T> src) {
        return nullSafeIsEmpty(src) ? Stream.empty() : src.stream();
    }

    /**
     * Null safe check if the collection contains provided item
     *
     * @return true if collection is not null and contains the item, false otherwise
     */
    public static <T> boolean nullSafeContains(Collection<T> collection, T item) {
        return collection != null && !collection.isEmpty() && collection.contains(item);
    }

}

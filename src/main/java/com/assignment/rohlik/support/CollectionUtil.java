package com.assignment.rohlik.support;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionUtil {

    public static boolean nullSafeIsEmpty(Collection src) {
        return src == null || src.isEmpty();
    }

    public static <T> Stream<T> nullSafeStream(Collection<T> src) {
        return nullSafeIsEmpty(src) ? Stream.empty() : src.stream();
    }

}

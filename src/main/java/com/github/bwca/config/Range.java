package com.github.bwca.config;

class Range<T> {
    public final T from;
    public final T to;

    public Range(T min, T max) {
        this.from = min;
        this.to = max;
    }
}

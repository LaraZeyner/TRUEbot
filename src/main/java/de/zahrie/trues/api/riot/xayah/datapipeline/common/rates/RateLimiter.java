package de.zahrie.trues.api.riot.xayah.datapipeline.common.rates;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface RateLimiter {
    interface ReservedPermit {
        void acquire();

        void cancel();
    }

    enum Type {
            BURST(FixedWindowRateLimiter.class);

        private final Class<? extends AbstractRateLimiter> clazz;

        Type(final Class<? extends AbstractRateLimiter> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends AbstractRateLimiter> getLimiterClass() {
            return clazz;
        }
    }

    void acquire() throws InterruptedException;

    boolean acquire(final long timeout, final TimeUnit unit) throws InterruptedException;

    <T> T call(final Callable<T> callable) throws Exception;

    <T> T call(final Callable<T> callable, final long timeout, final TimeUnit unit) throws Exception;

    void call(final Runnable runnable) throws InterruptedException;

    void call(final Runnable runnable, final long timeout, final TimeUnit unit) throws InterruptedException;

    int permitsIssued();

    void release();

    ReservedPermit reserve() throws InterruptedException;

    ReservedPermit reserve(final long timeout, final TimeUnit unit) throws InterruptedException;

    void restrict(long afterTime, TimeUnit afterUnit, long forTime, TimeUnit forUnit);

    void restrictFor(final long time, final TimeUnit unit);
}

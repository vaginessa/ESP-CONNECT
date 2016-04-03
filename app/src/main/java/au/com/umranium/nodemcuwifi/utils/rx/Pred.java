package au.com.umranium.nodemcuwifi.utils.rx;

import rx.functions.Func1;

/**
 * Utility class that adds predicate utility functions to {@link Func1} objects that return {@code
 * Boolean}.
 *
 * @author umran
 */
public abstract class Pred<T> implements Func1<T, Boolean> {

    public static <T> Pred<T> isNull() {
        return new IsNull<>();
    }

    public static <T> Pred<T> isEqual(T value) {
        return new IsEqual<>(value);
    }

    public static Pred<Boolean> not() {
        return new Not();
    }

    public Pred<T> negate() {
        return new Negate();
    }

    /**
     * Returns true if the value given is {@code null}
     *
     * @author umran
     */
    private static class IsNull<T> extends Pred<T> {
        @Override
        public Boolean call(T value) {
            return value == null;
        }
    }

    /**
     * Returns true if the value given is {@code null}
     *
     * @author umran
     */
    private static class IsEqual<T> extends Pred<T> {
        private final T mValue;

        public IsEqual(T value) {
            mValue = value;
        }

        @Override
        public Boolean call(T value) {
            if (mValue == null) {
                return value == null;
            } else {
                return mValue.equals(value);
            }
        }
    }

    /**
     * Returns true if the value given is {@code null}
     *
     * @author umran
     */
    private static class Not extends Pred<Boolean> {
        @Override
        public Boolean call(Boolean value) {
            if (value == null) {
                return null;
            }
            return !value;
        }
    }

    private class Negate extends Pred<T> {
        @Override
        public Boolean call(T value) {
            return !Pred.this.call(value);
        }
    }
}

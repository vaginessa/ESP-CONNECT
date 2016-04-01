package au.com.umranium.nodemcuwifi.utils.rx;

import rx.functions.Func1;

/**
 * @author umran
 */
public final class ToVoid<T> implements Func1<T, Void> {

    private static ToVoid sToVoid;

    public synchronized static <T> ToVoid<T> getInstance() {
        if (sToVoid == null) {
            sToVoid = new ToVoid();
        }
        return sToVoid;
    }

    private ToVoid() {
        // Do nothing
    }

    @Override
    public Void call(T t) {
        return null;
    }
}

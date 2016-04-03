package au.com.umranium.nodemcuwifi.utils.rx;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * An observable value. Emits an event whenever the value is set.
 *
 * @author umran
 */
public class ObservableValue<T> {

    private static final String TAG = ObservableValue.class.getSimpleName();

    private final Subject<T, T> mSubject;
    private T mValue;

    public ObservableValue(boolean onSubscribeEmitLast, T initialValue) {
        if (onSubscribeEmitLast) {
            mSubject = BehaviorSubject.create(initialValue);
        } else {
            mSubject = PublishSubject.create();
        }
        mValue = initialValue;
    }

    public ObservableValue(boolean onSubscribeEmitLast) {
        if (onSubscribeEmitLast) {
            mSubject = BehaviorSubject.create();
        } else {
            mSubject = PublishSubject.create();
        }
        mValue = null;
    }

    public T getValue() {
        return mValue;
    }

    synchronized
    public void setValue(T value) {
        mValue = value;
        mSubject.onNext(value);
    }

    public Observable<T> getObservable() {
        return mSubject;
    }

    public Observable<T> getNonNullObservable() {
        return mSubject
                .filter(Pred.isNull().negate());
    }

}
package au.com.umranium.espconnect.rx;

import java.util.Collection;

import rx.functions.Func1;

public class CollectionNotEmpty<T> implements Func1<Collection<T>, Boolean> {
  @Override
  public Boolean call(Collection<T> collection) {
    return !collection.isEmpty();
  }
}

package au.com.umranium.nodemcuwifi.api;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @author umran
 */
public interface NodeMcuService {

  @GET("/state")
  Observable<State> getState();

  @GET("/scan")
  Observable<ReceivedAccessPoints> scan();

}

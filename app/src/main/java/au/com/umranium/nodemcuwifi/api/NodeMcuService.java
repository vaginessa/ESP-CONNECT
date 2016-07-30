package au.com.umranium.nodemcuwifi.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author umran
 */
public interface NodeMcuService {

  @GET("/state")
  Observable<State> getState();

  @GET("/scan")
  Observable<ReceivedAccessPoints> scan();

  @GET("/wifisave")
  Observable<Void> save(@Query("s") String ssid, @Query("p") String password);

}

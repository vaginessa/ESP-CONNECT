package au.com.umranium.espconnect.api;

import au.com.umranium.espconnect.api.data.ReceivedAccessPoints;
import au.com.umranium.espconnect.api.data.State;
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

  @GET("/close")
  Observable<Void> close();

}

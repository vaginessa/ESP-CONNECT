package au.com.umranium.nodemcuwifi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

import javax.net.SocketFactory;
import java.net.InetAddress;

/**
 * @author umran
 */
public interface NodeMcuService {

  @GET("/state")
  Observable<State> getState();

  @GET("/scan")
  Observable<ReceivedAccessPoints> scan();

}

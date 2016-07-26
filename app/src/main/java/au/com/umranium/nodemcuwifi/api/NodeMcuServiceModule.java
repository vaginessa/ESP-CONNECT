package au.com.umranium.nodemcuwifi.api;

import android.net.Network;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;

import au.com.umranium.nodemcuwifi.di.activity.ActivityModule;
import au.com.umranium.nodemcuwifi.presentation.tasks.utils.WifiConnectionUtil;
import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ActivityModule.class)
public class NodeMcuServiceModule {

  private static final String HTTP_SCHEME = "http";
  private static final int HTTP_PORT = 80;

  @Provides
  public NodeMcuService provideService(WifiConnectionUtil connectionUtil) {
    InetAddress gateway = connectionUtil.getGateway();

    Gson gson = new GsonBuilder()
        .setLenient()
        .create();

    HttpUrl baseUrl = new HttpUrl.Builder()
        .scheme(HTTP_SCHEME)
        .host(gateway.getHostAddress())
        .port(HTTP_PORT)
        .build();

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Network network = connectionUtil.getWifiNetwork();
      clientBuilder.socketFactory(network.getSocketFactory());
    }
    OkHttpClient client = clientBuilder.build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(client)
        .build();

    return retrofit.create(NodeMcuService.class);
  }

}

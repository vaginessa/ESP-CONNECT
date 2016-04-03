package au.com.umranium.nodemcuwifi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;

import javax.net.SocketFactory;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

/**
 * @author umran
 */
public interface NodeMcuService {

    @GET("/state")
    Observable<State> getState();

    class Builder {
        private final InetAddress server;
        private SocketFactory mSocketFactory = null;

        public Builder(InetAddress server) {
            this.server = server;
        }

        public void setSocketFactory(SocketFactory socketFactory) {
            mSocketFactory = socketFactory;
        }

        public NodeMcuService build() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(State.class, new State.StateAdapter())
                    .setLenient()
                    .create();

            HttpUrl baseUrl = new HttpUrl.Builder()
                    .scheme("http")
                    .host(server.getHostAddress())
                    .port(80)
                    .build();

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (mSocketFactory != null) {
                clientBuilder.socketFactory(mSocketFactory);
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

}

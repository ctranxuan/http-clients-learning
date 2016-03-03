package org.ws13.learning.httpclients.asynchttpclient;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import rx.Observable;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

/**
 * @author ctranxuan
 */
public class AsyncHttpClientApp {

    public static void main(String[] args) {
        String url;
        url = "https://randomuser.me/api/";

        DefaultAsyncHttpClientConfig.Builder config;
        config = config()
                .setKeepAlive(true)
                .setMaxConnections(50)
                .setMaxConnectionsPerHost(50);

        ListenableFuture<Response> response;
        response = asyncHttpClient(config).prepareGet(url).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(final Response aResponse) throws Exception {
                System.out.println("AsyncHttpClientApp.onCompleted: " + aResponse.getResponseBody());
                return aResponse;
            }
        });

        Observable.from(response)
                .toBlocking()
                .subscribe(System.out::println);

    }
}

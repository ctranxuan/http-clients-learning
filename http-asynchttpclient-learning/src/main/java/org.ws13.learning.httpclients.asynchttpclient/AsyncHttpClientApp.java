package org.ws13.learning.httpclients.asynchttpclient;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

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


        Observable<Response> responseObservable = Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> aSubscriber) {
                ListenableFuture<Response> response;
                response = asyncHttpClient(config).prepareGet(url).execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(final Response aResponse) throws Exception {
                        System.out.println("AsyncHttpClientApp.onCompleted: " + aResponse.getResponseBody());
                        aSubscriber.onNext(aResponse);
                        aSubscriber.onCompleted();
                        return aResponse;
                    }

//                    @Override
//                    public void onThrowable(final Throwable t) {
//                        if (t instanceof CancellationException) {
//                            // silent cath due to miscompatibility between rxjava and asynchttpclient
//                            // see https://github.com/ReactiveX/RxJava/issues/1022
//                        } else {
//                            super.onThrowable(t);
//
//                        }
//                    }
                });

            }
        });

        Observable.interval(5, TimeUnit.SECONDS)
                .flatMap(l -> responseObservable)
                .toBlocking()
                .subscribe(System.out::println)
        ;

    }
}

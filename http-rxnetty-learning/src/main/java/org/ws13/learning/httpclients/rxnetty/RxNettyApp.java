package org.ws13.learning.httpclients.rxnetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurator;
import io.reactivex.netty.pipeline.ssl.DefaultFactories;
import io.reactivex.netty.pipeline.ssl.SSLEngineFactory;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientPipelineConfigurator;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;

/**
 * @author ctranxuan
 */
public class RxNettyApp {
    public static void main(String[] args) {

        HttpClient<ByteBuf, ByteBuf> client;
        client = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder("www.randomuser.me", 80)
                .withSslEngineFactory(new DefaultSSLEngineFactory())
                .build();

//        PipelineConfigurator<HttpClientResponse<ByteBuf>, HttpClientRequest<ByteBuf>> configurator;
//        configurator = new HttpClientPipelineConfigurator<>();
//
//        String host;
//        host = "www.randomuser.me";
//
//        HttpClient<ByteBuf, ByteBuf> client;
//        client = RxNetty.createHttpClient(host, 80, configurator);

        client.submit(HttpClientRequest.createGet("/api"))
                .flatMap(response ->
                            response.getContent().map(content -> content.toString(StandardCharsets.UTF_8)))
                .toBlocking()
                .subscribe(System.out::println, Throwable::printStackTrace);

    }

    private static class DefaultSSLEngineFactory implements SSLEngineFactory {

        private final SslContext sslCtx;

        private DefaultSSLEngineFactory() {
            try {
                SslProvider sslProvider = SslContext.defaultClientProvider();
                sslCtx = SslContext.newClientContext(sslProvider);
            } catch (SSLException e) {
                throw new IllegalStateException("Failed to create default SSL context", e);
            }
        }

        @Override
        public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
            return sslCtx.newEngine(allocator);
        }
    }
}

package com.example.wiremockforrsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@SpringBootApplication
public class WiremockForRsocketApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WiremockForRsocketApplication.class, args);
        Thread.currentThread().join();
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .dataMimeType(MimeType.valueOf("application/json"))
                .tcp("127.0.0.1", 9191);
    }
}

@Controller
class CrmClient {

    private final RSocketRequester rSocketRequester;

    CrmClient(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    Mono<Response> hello(String name) {
        return this.rSocketRequester
                .route("hello")
                .data(new Request(name))
                .retrieveMono(Response.class);
    }

    Flux<Cat> cats() {
        return this.rSocketRequester
                .route("cats")
                .data(new HashMap<>())
                .retrieveFlux(Cat.class);
    }

}

record Cat(String name) {
}

record Request(String name) {
}

record Response(String message) {
}
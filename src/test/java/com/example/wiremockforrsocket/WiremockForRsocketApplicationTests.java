package com.example.wiremockforrsocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.mock.rsocket.MessageMapping;
import org.springframework.mock.rsocket.RSocketMessageRegistry;
import org.springframework.mock.rsocket.RSocketServerExtension;
import reactor.test.StepVerifier;


@SpringBootTest
@ExtendWith(RSocketServerExtension.class)
class WiremockForRsocketApplicationTests {

    private final CrmClient crm;

    @Autowired
    WiremockForRsocketApplicationTests(
            @Value("${test.rsocket.server.port}") int port,
            RSocketRequester.Builder builder) {
        this.crm = new CrmClient(builder.tcp("127.0.0.1", port));
    }

    @Test
    void dynamicMapping(RSocketMessageRegistry catalog) {

        var messageMapping = MessageMapping
                .response("hello")
                .response(new Response("hello, Josh!"));
        catalog.register(messageMapping);

        var reply = this.crm.hello("Josh");
        StepVerifier
                .create(reply)
                .expectNext(new Response("hello, Josh!"))
                .verifyComplete();

    }

    @Test
    void automaticMapping () {
        var reply = this.crm.cats() ;
        StepVerifier
                .create(reply)
                .expectNext(new Cat ("Felix"))
                .expectNext(new Cat ("Garfield"))
                .expectNext(new Cat ("Mr. Bigglesworth"))
                .verifyComplete();
    }
}

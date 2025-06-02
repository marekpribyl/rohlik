package com.assignment.rohlik.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.FOUND;

@Hidden
@RestController
public class SwaggerController {

    @GetMapping("/")
    public Mono<ResponseEntity<Void>> rootToSwaggerUi() {
        return Mono.just(ResponseEntity
                .status(FOUND)
                .header("Location", "/swagger-ui.html")
                .build()
        );
    }

}

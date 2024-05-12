package com.iremkvkpnr.librarymanagement.controller.reactive;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.service.reactive.ReactiveBookService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/reactive/books")
public class ReactiveBookController {
    private final ReactiveBookService reactiveBookService;

    public ReactiveBookController(ReactiveBookService reactiveBookService) {
        this.reactiveBookService = reactiveBookService;
    }

    @GetMapping(value = "/availability", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Book> streamBookAvailability() {
        return reactiveBookService.streamBookAvailability();
    }
} 
package com.iremkvkpnr.librarymanagement.service.reactive;

import com.iremkvkpnr.librarymanagement.model.entity.Book;
import com.iremkvkpnr.librarymanagement.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.time.Duration;

@Service
public class ReactiveBookService {
    private final BookRepository bookRepository;

    public ReactiveBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Kitapların müsaitlik durumunu periyodik olarak stream eden örnek
    public Flux<Book> streamBookAvailability() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> Flux.fromIterable(bookRepository.findAll()));
    }
} 
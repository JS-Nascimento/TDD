package br.dev.jstec.library_api.api.service;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import br.dev.jstec.library_api.api.model.entity.Book;
import org.springframework.data.domain.Page;

public interface BookService {

	Book save(Book book);

Optional<Book> getById(Integer id);

void delete(Book book);

Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);
}

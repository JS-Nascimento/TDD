package br.dev.jstec.library_api.api.service;

import java.util.Optional;

import br.dev.jstec.library_api.api.model.entity.Book;

public interface BookService {

	Book save(Book book);

Optional<Book> getById(Integer id);

void delete(Book book);

Book update(Book book);
		
}

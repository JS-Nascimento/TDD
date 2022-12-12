package br.dev.jstec.library_api.service.impl;


import org.springframework.stereotype.Service;

import br.dev.jstec.library_api.api.model.entity.Book;
import br.dev.jstec.library_api.api.service.BookService;
import br.dev.jstec.library_api.model.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	
	public BookServiceImpl(BookRepository repository) {
	;
		this.repository = repository;
	}


	@Override
	public Book save(Book book) {
		// TODO Auto-generated method stub
		return repository.save(book);
	}

}

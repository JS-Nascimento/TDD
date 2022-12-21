package br.dev.jstec.library_api.service.impl;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.dev.jstec.library_api.api.exceptions.BusinessException;
import br.dev.jstec.library_api.api.model.entity.Book;
import br.dev.jstec.library_api.api.service.BookService;
import br.dev.jstec.library_api.model.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	
	public BookServiceImpl(BookRepository repository) {

		this.repository = repository;
	}


	@Override
	public Book save(Book book) {
		if (repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn Já cadastrado");
		}
		return repository.save(book);
	}


	@Override
	public Optional<Book> getById(Integer id) {
		// TODO Auto-generated method stub
		return this.repository.findById(id);
	}


	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null ) {
			throw new IllegalArgumentException("Book can't be null");
		}
		
		this.repository.delete(book);
		
	}


	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null ) {
			throw new IllegalArgumentException("Book can't be null");
		}
		
		return this.repository.save(book);
		
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example<Book> example = Example.of(filter,
				ExampleMatcher
						.matching()
						.withIgnoreCase()
						.withIgnoreNullValues()
						.withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
				);


		return repository.findAll(example, pageRequest);
	}

}

package br.dev.jstec.library_api.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.jstec.library_api.api.model.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book , Integer>{
		boolean existsByIsbn(String isbn);
}

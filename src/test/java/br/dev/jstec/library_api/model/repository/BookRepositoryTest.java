package br.dev.jstec.library_api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.dev.jstec.library_api.api.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	
	@Test
	@DisplayName("Must return TRUE when exist one book with the duplicated ISBN.")
	public void returnTrueWhenISBNExist() {
		String isbn = "123";
		
		Book book = Book.builder().title("As aventuras").author("fulano").isbn(isbn).build();
		
		entityManager.persist(book);
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isTrue();
	}
	@Test
	@DisplayName("Must return FALSE when don't exist book with the duplicated ISBN.")
	public void returnFalseWhenISBNNotExist() {
		String isbn = "123";
					
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Must return a Book per ID")
	public void findByIdTest() {
		String isbn = "123";
		Book book = Book.builder().title("As aventuras").author("fulano").isbn(isbn).build();			
		entityManager.persist(book);
		
		Optional<Book> foundBook = repository.findById(book.getId());
		
		assertThat(foundBook.isPresent()).isTrue();
	}
}

package br.dev.jstec.library_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.dev.jstec.library_api.api.exceptions.BusinessException;
import br.dev.jstec.library_api.api.model.entity.Book;
import br.dev.jstec.library_api.api.service.BookService;
import br.dev.jstec.library_api.model.repository.BookRepository;
import br.dev.jstec.library_api.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
	
	BookService service;
	@MockBean
	BookRepository repository;
	
	
	@BeforeEach
	public void  setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve Salvar um Livro")
	public void saveBookTest() {
		//cenário
		
		Book book = createValidBook();
		Mockito.when(repository.save(book)).thenReturn(
													Book.builder()
															.id(11)
															.title("As Aventuras")
															.author("Fulano")
															.isbn("123")
															.build()
																				);
		
		Book savedBook = service.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
			
	}

	private Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As Aventuras").build();
	}
	
	@Test
	@DisplayName("Deve lançar erro de negocio ao tentar salvar um livro como isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {
		
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		assertThat(exception)
							.isInstanceOf(BusinessException.class)
							.hasMessage("Isbn Já cadastrado");
		
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
		

}

package br.dev.jstec.library_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	
	@Test
	@DisplayName("Deve retornar o Livro por ID")
	public void getByIdTest () {
		
		Integer id = 1;
		Book book = createValidBook();
		book.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> foundBook = service.getById(id);
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(book.getId());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao obter Livro por ID inexistente")
	public void getNonExistentByIdTest () {
		
		Integer id = 1;
				
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> foundBook = service.getById(id);
		
		assertThat(foundBook.isPresent()).isFalse();
		
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
	
	@Test
	@DisplayName("Deve deletar um Livro existente")
	public void deleteBookTest() {
		
		Book book = Book.builder().id(1).build();
		
		org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));
	
		Mockito.verify(repository, Mockito.times(1)).delete(book);
		
	}
	
	@Test
	@DisplayName("Deve lançar um erro ao tentar excluir um livro inexistente.")
	public void deleteInvalidBookTest() {
		
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class ,() -> service.delete(book));
	
		Mockito.verify(repository, Mockito.never()).delete(book);
		
	}
	@Test
	@DisplayName("Deve lançar um erro ao tentar atualizar um livro inexistente.")
	public void updateInvalidBookTest() {
		
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class ,() -> service.update(book));
	
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
	@Test
	@DisplayName("Deve atualizar um Livro existente")
	public void updateBookTest() {
		Integer id = 1;
		Book updatingBook = Book.builder().id(1).build();
		
		
		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		
		Book book = service.update(updatingBook);
		
		assertThat(book.getId()).isEqualTo(book.getId());
		assertThat(book.getIsbn()).isEqualTo(book.getIsbn());
		assertThat(book.getTitle()).isEqualTo(book.getTitle());
		assertThat(book.getAuthor()).isEqualTo(book.getAuthor());
		
		
	}
	@Test
	@DisplayName("Must return selected books per attributes")
	public void findBookTest() {

		//scene
		Book book  = createValidBook();
		PageRequest pageRequest = PageRequest.of(0,10);

		List<Book> list = Arrays.asList(book);
		Page<Book> page = new PageImpl<Book>( list, pageRequest, 1);
		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
				.thenReturn(page);

		//action
		Page<Book> result = service.find(book, pageRequest);

		//verifications
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(list);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
		

}

package br.dev.jstec.library_api.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dev.jstec.library_api.api.DTO.BookDTO;
import br.dev.jstec.library_api.api.exceptions.BusinessException;
import br.dev.jstec.library_api.api.model.entity.Book;
import br.dev.jstec.library_api.api.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService service;

	@Test
	@DisplayName("Deve Criar um Livro com Sucesso")
	public void createBookTest() throws Exception {

		BookDTO dto = createNewBook();
		Book savedBook = Book.builder().id(101).author("Artur").title("As aventuras").isbn("001").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isCreated()).andExpect(jsonPath("id").value(101))
				.andExpect(jsonPath("title").value(dto.getTitle())).andExpect(jsonPath("author").value(dto.getAuthor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()))

		;

	}

	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes")
	public void createIvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(3)));


	}

	@Test
	@DisplayName("Deve lançar erro de validação quando cadastrar ISBN repetido")
	public void createBookWithISBNDuplicated() throws Exception {

		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String errorMessage = "Isbn Já cadastrado";

		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(errorMessage));
	}

	private BookDTO createNewBook() {
		return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
	}

	@Test
	@DisplayName("Must get details about a book.")
	public void getBookDetailsTest() throws Exception {
		Integer id = 1;

		Book book = Book.builder()
								.id(id)
								.title(createNewBook().getTitle())
								.author(createNewBook().getAuthor())
								.isbn(createNewBook().getIsbn())
								.build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.get(BOOK_API.concat("/"+id))
											.accept(MediaType.APPLICATION_JSON);
										
		mvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
		
	}
	
	@Test
	@DisplayName("should return resource not found when a searched book does not exist")
	public void bookNotFoundTest() throws Exception{
		
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.get(BOOK_API.concat("/"+ 1 ))
											.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
		.andExpect(status().isNotFound());
		
		
	}
	@Test
	@DisplayName("Must delete a one book")
	public void deleteBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.of(Book.builder().id(1).build()));

		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.delete(BOOK_API.concat("/"+ 1 ))
											.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
		.andExpect(status().isNoContent());
		
	}
	
	@Test
	@DisplayName("Must return status not found when a book do not found to delete")
	public void deleteInexistentBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.delete(BOOK_API.concat("/"+ 1 ))
											.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
		.andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Must return status not found when update a non-existent book")
	public void updateNonExistentBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.delete(BOOK_API.concat("/"+ 1 ))
											.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
		.andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Must uptade a book")
	public void updateBookTest() throws Exception {
		
		Integer id = 1;
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		
		Book updatingBook = Book.builder().title("Some Title").author("Some Author").isbn("456").build();
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.of(updatingBook));

		Book updatedBook = Book.builder().id(1).author("Artur").title("As aventuras").isbn("001").build();
		BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);
		
		MockHttpServletRequestBuilder request = 
					MockMvcRequestBuilders.put(BOOK_API.concat("/"+ 1 ))
											.content(json)
											.accept(MediaType.APPLICATION_JSON)
											.contentType(MediaType.APPLICATION_JSON);
											
		mvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(jsonPath("id").value(id))
		.andExpect(jsonPath("title").value(createNewBook().getTitle()))
		.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
		.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
		

		
	}
	
	

}

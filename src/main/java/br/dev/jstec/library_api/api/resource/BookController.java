package br.dev.jstec.library_api.api.resource;


import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.dev.jstec.library_api.api.DTO.BookDTO;
import br.dev.jstec.library_api.api.exceptions.ApiErrors;
import br.dev.jstec.library_api.api.exceptions.BusinessException;
import br.dev.jstec.library_api.api.model.entity.Book;
import br.dev.jstec.library_api.api.service.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private BookService service;
	private ModelMapper modelMapper;
	
	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.modelMapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create( @RequestBody @Valid BookDTO dto ) {
		Book entity = modelMapper.map(dto, Book.class);
		

		entity = service.save(entity);
		return modelMapper.map(entity, BookDTO.class);
	}
	
	@GetMapping("{id}")
	public BookDTO get(@PathVariable Integer id) {
		
		return service
					.getById(id)
					.map(book -> modelMapper.map(book, BookDTO.class))
					.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
			
	}
	@GetMapping()
	public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {

		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		List<BookDTO> list = result.getContent()
				.stream()
				.map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements() );




	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		
		Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));;
		service.delete(book);
		
	}


	
	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO update(@PathVariable Integer id, BookDTO dto) {
		
		return service.getById(id).map( book -> {
			book.setAuthor(dto.getAuthor());
			book.setTitle(dto.getTitle());
			book.setIsbn(dto.getIsbn());
			book = service.update(book);
			return modelMapper.map(book, BookDTO.class);
			
		}).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		
		
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		
		BindingResult bindingResult = ex.getBindingResult();
		
		return new ApiErrors(bindingResult);
		
	}
	

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessExceptions(BusinessException ex) {
			
		return new ApiErrors(ex);
		
	}
	
	

}

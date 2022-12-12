package br.dev.jstec.library_api.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	private Integer id;
	private String title;
	private String author;
	private String isbn;
}
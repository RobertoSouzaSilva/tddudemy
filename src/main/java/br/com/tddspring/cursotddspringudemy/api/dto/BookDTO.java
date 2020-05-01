package br.com.tddspring.cursotddspringudemy.api.dto;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	private Integer id;
	@NotEmpty
	private String title;
	@NotEmpty
	private String author;
	@NotEmpty
	private String isbn;


}

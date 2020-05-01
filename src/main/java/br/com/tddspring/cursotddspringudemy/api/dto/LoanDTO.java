package br.com.tddspring.cursotddspringudemy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
	
	private Integer id;
	private String isbn;
	private String customer;
	private BookDTO book;
	private String email;


}

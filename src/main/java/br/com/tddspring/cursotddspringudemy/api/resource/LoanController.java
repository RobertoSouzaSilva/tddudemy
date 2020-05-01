package br.com.tddspring.cursotddspringudemy.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.tddspring.cursotddspringudemy.api.dto.BookDTO;
import br.com.tddspring.cursotddspringudemy.api.dto.LoanDTO;
import br.com.tddspring.cursotddspringudemy.api.dto.LoanFilterDTO;
import br.com.tddspring.cursotddspringudemy.api.dto.ReturnedLoanDTO;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.entity.Loan;
import br.com.tddspring.cursotddspringudemy.service.BookService;
import br.com.tddspring.cursotddspringudemy.service.LoanService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/loans")
public class LoanController {

	private final LoanService service;
	private final BookService bookService;
	private final ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Integer create(@RequestBody LoanDTO dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
		Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();
		entity = service.save(entity);
		return entity.getId();
	}
	
	@PatchMapping("{id}")
	public void returnBook(@PathVariable Integer id, @RequestBody ReturnedLoanDTO dto) {
		Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());
		service.update(loan);
	}
	
	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest){
		Page<Loan> result = service.find(dto, pageRequest);
		List<LoanDTO> loans =result.getContent()
		.stream()
		.map(entity -> {
			Book book = entity.getBook();
			BookDTO bookDto = modelMapper.map(book, BookDTO.class);
			LoanDTO loanDto = modelMapper.map(entity, LoanDTO.class);
			loanDto.setBook(bookDto);
			return loanDto;
			}).collect(Collectors.toList());
		return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
	}
	
}
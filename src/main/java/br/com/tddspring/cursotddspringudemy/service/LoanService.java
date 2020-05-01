package br.com.tddspring.cursotddspringudemy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tddspring.cursotddspringudemy.api.dto.LoanFilterDTO;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.entity.Loan;

@Service
public interface LoanService {

	Loan save(Loan loan);

	Optional<Loan> getById(Integer id);
	
	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
	
	Page<Loan> getLoansByBook(Book book, Pageable pageable);
	
	List<Loan> getAllLateLoans();

}

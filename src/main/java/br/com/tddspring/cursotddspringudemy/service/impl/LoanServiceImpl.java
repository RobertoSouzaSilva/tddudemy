package br.com.tddspring.cursotddspringudemy.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.tddspring.cursotddspringudemy.api.dto.LoanFilterDTO;
import br.com.tddspring.cursotddspringudemy.exception.BusinessException;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.entity.Loan;
import br.com.tddspring.cursotddspringudemy.model.repository.LoanRepository;
import br.com.tddspring.cursotddspringudemy.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private LoanRepository repository;
	
	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("book already loaned");
		}
		return repository.save(loan);
	}

	@Override
	public Optional<Loan> getById(Integer id) {
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
		return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		return repository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanDays = 4;
		LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
		return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
	}
}

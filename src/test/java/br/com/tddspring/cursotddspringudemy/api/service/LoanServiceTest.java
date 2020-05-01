package br.com.tddspring.cursotddspringudemy.api.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import br.com.tddspring.cursotddspringudemy.api.dto.LoanFilterDTO;
import br.com.tddspring.cursotddspringudemy.exception.BusinessException;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.entity.Loan;
import br.com.tddspring.cursotddspringudemy.model.repository.LoanRepository;
import br.com.tddspring.cursotddspringudemy.service.LoanService;
import br.com.tddspring.cursotddspringudemy.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

	LoanService service;
	
	@MockBean
	LoanRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new LoanServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um emprestimo")
	public void saveLoantest() {
		Book book = Book.builder().id(1).build();
		String customer = "fulano";
		
		Loan savingLoan = Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
		Loan savedLoan = Loan.builder().id(1).loanDate(LocalDate.now()).customer(customer).book(book).build();
		
		Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
		Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);
		
		Loan loan = service.save(savingLoan);
		
		org.assertj.core.api.Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		org.assertj.core.api.Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
		org.assertj.core.api.Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		org.assertj.core.api.Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
	}
	
	@Test
	@DisplayName("Deve lançar erro de negocio ao salvar um empréstimo com livro ja emprestado")
	public void loanedBookSaveTest() {
		Book book = Book.builder().id(1).build();
		String customer = "fulano";
		
		Loan savingLoan = Loan
				.builder()
				.book(book)
				.customer(customer)
				.loanDate(LocalDate.now())
				.build();
		
		Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.save(savingLoan));
		
		org.assertj.core.api.Assertions.assertThat(exception)
		.isInstanceOf(BusinessException.class)
		.hasMessage("book already loaned");
		
		Mockito.verify(repository, Mockito.never()).save(savingLoan);
	}
	
	@Test
	@DisplayName("Deve obter as inforações de um emprestimo pelo id")
	public void getLoanDetailsTest() {
		Integer id = 1;
		
		Loan loan = createLoan();
		loan.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
		
		Optional<Loan> result = service.getById(id);
		
		org.assertj.core.api.Assertions.assertThat(result.isPresent()).isTrue();
		org.assertj.core.api.Assertions.assertThat(result.get().getId()).isEqualTo(id);
		org.assertj.core.api.Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		org.assertj.core.api.Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());
		org.assertj.core.api.Assertions.assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
		
		Mockito.verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Deve Atualizar um emprestimo")
	public void updateLoanTest() {
		Loan loan = createLoan();
		loan.setId(1);
		loan.setReturned(true);
		
		Mockito.when(repository.save(loan)).thenReturn(loan);
		
		Loan updatedLoan = service.update(loan);
		
		org.assertj.core.api.Assertions.assertThat(updatedLoan.getReturned()).isTrue();
		Mockito.verify(repository).save(loan);
	}
	
	@Test
	@DisplayName("Deve filtrar empresitmos pelas propriedades")
	public void findLoanTest() {
		LoanFilterDTO lfDto = LoanFilterDTO.builder().customer("fulano").isbn("321").build();

		Loan loan = createLoan();
		loan.setId(1);

		PageRequest pageRequest = PageRequest.of(0, 10);

		List<Loan> lista = Arrays.asList(loan);
		Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
		Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
				.thenReturn(page);

		Page<Loan> result = service.find(lfDto, pageRequest);

		org.assertj.core.api.Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
		org.assertj.core.api.Assertions.assertThat(result.getContent()).isEqualTo(lista);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
	
	public static Loan createLoan(){
		Book book = Book.builder().id(1).build();
		String customer = "fulano";
		
		return Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();
	}
}

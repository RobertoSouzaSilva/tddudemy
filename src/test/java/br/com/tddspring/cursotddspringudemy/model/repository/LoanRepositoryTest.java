package br.com.tddspring.cursotddspringudemy.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	private LoanRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("deve verificar se existe empréstimo não devolvido para o livro.")
	public void existsByBookAndNotReturnedTest() {

		Loan loan = createAndPersistLoan(LocalDate.now());
		Book book = loan.getBook();

        boolean exists = repository.existsByBookAndNotReturned(book);

		org.assertj.core.api.Assertions.assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve buscar um emprestimo pelo isbn do livro ou pelo customer")
	public void findByBookIsbnOrCustomer() {
		Loan loan = createAndPersistLoan(LocalDate.now());
		
		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "fulalo", PageRequest.of(0, 10));
		
		org.assertj.core.api.Assertions.assertThat(result.getContent()).hasSize(1);
		org.assertj.core.api.Assertions.assertThat(result.getContent()).contains(loan);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		org.assertj.core.api.Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
	}
	
	@Test
    @DisplayName("Deve obter empréstimos cuja data emprestimo for menor ou igual a tres dias atras e nao retornados")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        org.assertj.core.api.Assertions.assertThat(result).hasSize(1).contains(loan);
    }
	
	@Test
    @DisplayName("deve retornar vazio quando nao houver emprestimos atrasados")
    public void notFindByLoanDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(LocalDate.now());

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        org.assertj.core.api.Assertions.assertThat(result).isEmpty();
    }
	
	
	
	public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = BookRepositoryTest.createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }
	
}

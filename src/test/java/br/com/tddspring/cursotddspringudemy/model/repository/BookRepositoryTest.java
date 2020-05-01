package br.com.tddspring.cursotddspringudemy.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
	
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository bookRepository;
	
	@Test
	@DisplayName("deve retornar verdadeiro quando existir um libro na base com o isbn informado")
	public void returnTrueWhenIsbnExists() {
		
		String isbn = "123";
		Book book =  createNewBook(isbn);
		entityManager.persist(book);
		
		boolean exists = bookRepository.existsByIsbn(isbn);
		
		org.assertj.core.api.Assertions.assertThat(exists).isTrue();
	}
	
	public static Book createNewBook(String isbn) {
		return Book.builder().isbn(isbn).author("Fulano").title("As aventuras").build();
	}
	
	@Test
	@DisplayName("deve retornar verdadeiro quando existir um libro na base com o isbn informado")
	public void returnFalseWhenIsbnDoesntExists() {
		
		String isbn = "123";
		
		
		boolean exists = bookRepository.existsByIsbn(isbn);
		
		org.assertj.core.api.Assertions.assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Deve obter um livro por id")
	public void findByIdService() {
		String isbn = "123";
		Book book =  createNewBook(isbn);
		entityManager.persist(book);
		Optional<Book> foundBook = bookRepository.findById(book.getId());
		org.assertj.core.api.Assertions.assertThat(foundBook.isPresent()).isTrue();
	}
	
	@Test
	@DisplayName("DEve salvar um livro")
	public void saveBookTest() {
		Book book = createNewBook("123");
		Book savedBook = bookRepository.save(book);
		org.assertj.core.api.Assertions.assertThat(savedBook.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("DEve deletar m livro")
	public void deleteBookTest() {
		String isbn = "123";
		Book book =  createNewBook(isbn);
		entityManager.persist(book);
		Book bookFound = entityManager.find(Book.class, book.getId());
		bookRepository.delete(bookFound);
		Book deletedBook = entityManager.find(Book.class, book.getId());
		org.assertj.core.api.Assertions.assertThat(deletedBook).isNull();
	}
	

}

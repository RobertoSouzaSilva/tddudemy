package br.com.tddspring.cursotddspringudemy.api.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
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

import br.com.tddspring.cursotddspringudemy.exception.BusinessException;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.model.repository.BookRepository;
import br.com.tddspring.cursotddspringudemy.service.BookService;
import br.com.tddspring.cursotddspringudemy.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;

	@MockBean
	BookRepository bookRepository;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(bookRepository);
	}

	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		Book book = createValidBook();
		Mockito.when(bookRepository.save(book))
				.thenReturn(Book.builder().id(101).isbn("123").author("Fulano").title("As aventuras").build());

		Book savedBook = service.save(book);

		org.assertj.core.api.Assertions.assertThat(savedBook.getId()).isNotNull();
		org.assertj.core.api.Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
		org.assertj.core.api.Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
		org.assertj.core.api.Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
	}

	@Test
	@DisplayName("Deve lançar um erro de negocio ao tentarr salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() throws Exception {

		Book book = createValidBook();
		Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(BusinessException.class)
				.hasMessage("Isbn já cadastrado.");

		Mockito.verify(bookRepository, Mockito.never()).save(book);
	}

	@Test
	@DisplayName("Deve Obter um livro por id")
	public void getByIdTest() {
		Integer id = 1;
		Book book = createValidBook();
		book.setId(id);
		Mockito.when(service.getById(id)).thenReturn(Optional.of(book));

		Optional<Book> foundBook = service.getById(id);

		org.assertj.core.api.Assertions.assertThat(foundBook.isPresent()).isTrue();
		org.assertj.core.api.Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
		org.assertj.core.api.Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		org.assertj.core.api.Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		org.assertj.core.api.Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
	}

	@Test
	@DisplayName("Deve retornar vazio ao obter um livro por id quando ele nao existe na base")
	public void getNotFoundByIdTest() {
		Integer id = 1;
		Mockito.when(service.getById(id)).thenReturn(Optional.empty());

		Optional<Book> book = service.getById(id);

		org.assertj.core.api.Assertions.assertThat(book.isPresent()).isFalse();

	}

	@Test
	@DisplayName("Deve Deletar um Livro")
	public void deleteBookTest() {
		Book book = Book.builder().id(1).build();
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
		Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
	}

	@Test
	@DisplayName("Deve ocorrer o erro ao tentar deletar um livro inxistente")
	public void deleteInvalidBookTest() {
		Book book = new Book();
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		Mockito.verify(bookRepository, Mockito.never()).delete(book);
	}

	@Test
	@DisplayName("Deve ocorrer o erro ao tentar atualizar um livro inxistente")
	public void updateInvalidBookTest() {
		Book book = new Book();
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
		Mockito.verify(bookRepository, Mockito.never()).save(book);
	}

	@Test
	@DisplayName("Deve atualizar um livro.")
	public void updateBookTest() {
		Integer id = 1;

		Book updatingBook = Book.builder().id(id).build();

		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

		Book book = service.update(updatingBook);

		org.assertj.core.api.Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
		org.assertj.core.api.Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
		org.assertj.core.api.Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
		org.assertj.core.api.Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

	}

	@Test
	@DisplayName("Deve filtrar livros pelas propriedades")
	public void findBookTest() {
		Book book = createValidBook();

		PageRequest pageRequest = PageRequest.of(0, 10);

		List<Book> lista = Arrays.asList(book);
		Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
		Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
				.thenReturn(page);

		Page<Book> result = service.find(book, pageRequest);

		org.assertj.core.api.Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
		org.assertj.core.api.Assertions.assertThat(result.getContent()).isEqualTo(lista);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
	@Test
	@DisplayName("deve obter um livro pelo isbn")
	public void getBookByIsbnTest() {
		String isbn = "1230";
		Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1).isbn(isbn).build()));
		Optional<Book> book = service.getBookByIsbn(isbn);
		
		org.assertj.core.api.Assertions.assertThat(book.isPresent()).isTrue();
		org.assertj.core.api.Assertions.assertThat(book.get().getId()).isEqualTo(1);
		org.assertj.core.api.Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);
		
		Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
	}

	private Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}

}

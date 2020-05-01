package br.com.tddspring.cursotddspringudemy.api.resource;

import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tddspring.cursotddspringudemy.api.dto.BookDTO;
import br.com.tddspring.cursotddspringudemy.exception.BusinessException;
import br.com.tddspring.cursotddspringudemy.model.entity.Book;
import br.com.tddspring.cursotddspringudemy.service.BookService;
import br.com.tddspring.cursotddspringudemy.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	private BookService service;
	
	@MockBean
	private LoanService loanService;	

	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {

		BookDTO book = createNewBook();
		Book savedBook = Book.builder().id(101).author("Rock").title("As Aventuras").isbn("234").build();
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
		String json = new ObjectMapper().writeValueAsString(book);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));

	}

	@Test
	@DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para criação do livro")
	public void createIvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
	}

	@Test
	@DisplayName("Deve lançar um erro ao tentar cadastrar um livro com isbn ja utilizado por outro")
	public void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String messageError = "isbn ja cadastrado.";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(messageError));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(messageError));
	}

	@Test
	@DisplayName("DEve obter informações de um livro")
	public void getBookDetailsTest() throws Exception {
		Integer id = 1;

		Book book = Book.builder().id(id).title(createNewBook().getTitle()).author(createNewBook().getAuthor())
				.isbn(createNewBook().getIsbn()).build();

		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));

	}

	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
	public void BookNotFoundTest() throws Exception {

		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());

	}

	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.of(Book.builder().id(11).build()));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());

	}

	@Test
	@DisplayName("Deve retornar resource not found quando encontrar um livro para deletar")
	public void deleteInexistentBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());

	}

	@Test
	@DisplayName("Deve Atualizar um Livro")
	public void updateBookTest() throws Exception {

		Integer id = 1;
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		Book updatingBook = Book.builder().id(1).author("some author").title("some title").isbn("123").build();
		Book updatedBook = Book.builder().id(id).author("Rock").title("As Aventuras").isbn("234").build();
		BDDMockito.given(service.getById(1)).willReturn(Optional.of(updatingBook));
		BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1)).content(json)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
		;

	}

	@Test
	@DisplayName("DEve retornar 404 ao tentar atualizar um livro inxistente")
	public void updateInexistentBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		BDDMockito.given(service.getById(Mockito.anyInt())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1)).content(json)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception{

        Integer id = 1;

        Book book = Book.builder()
                    .id(id)
                    .title(createNewBook().getTitle())
                    .author(createNewBook().getAuthor())
                    .isbn(createNewBook().getIsbn())
                    .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Book>( Arrays.asList(book), PageRequest.of(0,100), 1 )   );

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1) )
            .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0))
            ;
    }

	private BookDTO createNewBook() {
		return BookDTO.builder().author("Rock").title("As Aventuras").isbn("234").build();
	}

}

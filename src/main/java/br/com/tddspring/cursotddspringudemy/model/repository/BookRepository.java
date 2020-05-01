package br.com.tddspring.cursotddspringudemy.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.tddspring.cursotddspringudemy.model.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

	boolean existsByIsbn(String isbn);

	Optional<Book> findByIsbn(String isbn);

}

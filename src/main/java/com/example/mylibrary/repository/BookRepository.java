package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.enums.CategoryName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllByCategoryName(CategoryName name, Pageable pageable);

    Page<Book> findAllByCategoryNameAndTitleContaining(CategoryName category, String title, Pageable pageable);

    Page<Book> findByTitleContaining(String title, Pageable pageable);

    Page<Book> findAll(Pageable pageable);
}

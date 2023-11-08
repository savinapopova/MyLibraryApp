package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.enums.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByCategoryName(CategoryName name);

    List<Book> findAllByCategoryNameAndTitleContaining(CategoryName category, String title);

    List<Book> findByTitleContaining(String title);
}

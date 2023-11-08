package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public String search(Model model) {

        return "redirect:search/all";
    }

    @GetMapping("/search/{category}")
    public String search(@PathVariable("category") String category,
                         Model model) {


        List<SearchBookDTO> books = this.bookService.getBooksByCategory(category);

        model.addAttribute("books", books);
        return "search";
    }

    @GetMapping("/books/{id}")
    public String getBookById() {
        return "book-details";
    }

    @PostMapping("/search/{category}")
    public String searchByTitle(@PathVariable(value = "category", required = false) String category,
            @RequestParam(value = "title", required = false) String title,
                         Model model) {

        List<SearchBookDTO> books = this.bookService.getBooksByTitleAndCategory(title,category);

        model.addAttribute("books", books);

        return "search";
    }


}

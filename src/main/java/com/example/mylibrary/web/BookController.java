package com.example.mylibrary.web;


import com.example.mylibrary.model.dto.book.SearchBookDTO;
import com.example.mylibrary.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public String search(Model model) {
        return "redirect:search/all";
    }

    @GetMapping("/search/{category}")
    public String search(@PathVariable("category") String category,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         @RequestParam(name = "size", defaultValue = "5") int size,
                         Model model) {


        Page<SearchBookDTO> booksPage = this.bookService.getBooksByCategory(category, PageRequest.of(page - 1, size));
        prepareModelAttributes(booksPage, model, size);


        return "search";
    }

    @GetMapping("/books/{id}")
    public String getBookById() {
        return "book-details";
    }

    @PostMapping("/search/{category}")
    public String searchByTitle(@PathVariable(value = "category", required = false) String category,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "size", defaultValue = "5") int size,
                                @RequestParam(value = "title", required = false) String title,
                                Model model,
                                RedirectAttributes redirectAttributes   ) {

        Page<SearchBookDTO> booksPage = this.bookService.getBooksByTitleAndCategory(title, category, PageRequest.of(page - 1, size));
        prepareModelAttributes(booksPage, model, size);

        if (title != null) {
            model.addAttribute("title", title);
            redirectAttributes.addFlashAttribute(title);
        }



        return "search";
    }

    private void prepareModelAttributes(Page<SearchBookDTO> booksPage, Model model, int size) {
        List<SearchBookDTO> books = booksPage.getContent();
        long total = booksPage.getTotalElements();

        model.addAttribute("books", books);
        model.addAttribute("currentPage", booksPage.getNumber() + 1);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        int indexOfLastBook = (booksPage.getNumber() + 1) * size;
        int indexOfFirstBook = indexOfLastBook - size;
        int lastItem = (int) Math.min(indexOfLastBook, total);

        model.addAttribute("indexOfFirstBook", indexOfFirstBook);
        model.addAttribute("indexOfLastBook", indexOfLastBook);
        model.addAttribute("lastItem", lastItem);
        model.addAttribute("totalAmountOfBooks", total);
    }

}

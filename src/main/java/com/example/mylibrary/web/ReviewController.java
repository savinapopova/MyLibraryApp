package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.LeaveReviewDTO;
import com.example.mylibrary.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ReviewController {

    @ModelAttribute
    public LeaveReviewDTO init() {
        return new LeaveReviewDTO();
    }

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/form/{id}")
    public String leaveReview(@PathVariable ("id") Long id, Model model, Principal principal) {

        model.addAttribute("bookId", id);

        boolean reviewLeft = this.reviewService.reviewLeft(principal, id);

        model.addAttribute("reviewLeft", reviewLeft);


        return "review-add";
    }

    @PostMapping("/reviews/leave/{id}")
    public String leaveReview(@Valid LeaveReviewDTO leaveReviewDTO, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, @PathVariable ("id") Long id, Principal principal) {


            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("leaveReviewDTO", leaveReviewDTO);
                redirectAttributes
                        .addFlashAttribute("org.springframework.validation.BindingResult.leaveReviewDTO",
                                bindingResult);
                return "redirect:/reviews/form/" + id;
            }

            this.reviewService.registerReview(leaveReviewDTO, principal, id);



        return "redirect:/search";
    }
}

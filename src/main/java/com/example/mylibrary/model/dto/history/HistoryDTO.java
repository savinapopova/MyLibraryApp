package com.example.mylibrary.model.dto.history;

import com.example.mylibrary.model.dto.book.SearchBookDTO;
import lombok.Data;

@Data
public class HistoryDTO {

    private SearchBookDTO book;

    private String checkoutDate;

    private String returnDate;

    public HistoryDTO() {
    }
}

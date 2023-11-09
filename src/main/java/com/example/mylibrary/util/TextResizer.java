package com.example.mylibrary.util;

import com.example.mylibrary.model.entity.Book;

import java.util.Arrays;

public class TextResizer {

    public static Book resizeDescription(Book book) {
        String description = book.getDescription();
        char[] chars = description.toCharArray();
        if (chars.length <= 320) {
            return book;
        }
StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 320; i++) {
            builder.append(chars[i]);
        }
        String correctedDescription = builder.toString();
        book.setDescription(correctedDescription);
        return book;
    }
}

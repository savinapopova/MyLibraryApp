package com.example.mylibrary.util;

import com.example.mylibrary.model.dto.CheckOutDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimeConverter {

    public static long getTimeDifference(CheckOutDTO checkout) {



        LocalDate returnDate = checkout.getReturnDate();
        LocalDate nowDate = LocalDate.now();

        long daysBetween = ChronoUnit.DAYS.between(nowDate, returnDate);

        return daysBetween;

    }
}

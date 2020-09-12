package ru.family.auth.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UtilsHolder {

    public static String generate2FaCode() {
        return String.valueOf(new Random().nextInt(9999));
    }

}

package ru.sibdigital.addcovid.frontend.util;

import java.util.ArrayList;
import java.util.List;

public class CharsAndSymbolsGenerator {

    final private static List<Character> rusChars = new ArrayList<>(70);
    final private static List<Character> engChars = new ArrayList<>(70);



    static {
        for (int i = 'А'; i <= 'Я'; i++) {
            rusChars.add((char) i);
        }
        rusChars.add(' ');
        rusChars.add('Ё');
        rusChars.add('ё');
        for (int i = 'а'; i <= 'я'; i++) {
            rusChars.add((char) i);
        }

        for (int i = 'A'; i <= 'Z'; i++) {
            engChars.add((char) i);
        }
        for (int i = 'a'; i <= 'z'; i++) {
            engChars.add((char) i);
        }
    }

    public static String generateWord(int maximumNumberOfChars){

        int sizeOfSequence = getRandomNumber(1,maximumNumberOfChars);

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            word.append(rusChars.get(getRandomNumber(0,rusChars.size()-1)));
        }

        return word.toString();
    }

    public static String generateEmail(int sizeOfSequence){


        StringBuilder word = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            if(i == sizeOfSequence/2) {
                word.append("@");
            } else if(i == sizeOfSequence-3){
                word.append(".");
            } else {
                word.append(engChars.get(getRandomNumber(0,engChars.size()-1)));
            }
        }

        return word.toString();
    }

    public static int getRandomNumber(int min, int max) {
        return min + (int) (Math.random() * max-1);
    }

    public static String generateNumberSequence(int sizeOfSequence){
        StringBuilder sequence = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            sequence.append(getRandomNumber(0,9));
        }
        return sequence.toString();
    }
}

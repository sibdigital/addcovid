package ru.sibdigital.addcovid.service;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);
}

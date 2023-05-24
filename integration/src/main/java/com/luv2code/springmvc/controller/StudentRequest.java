package com.luv2code.springmvc.controller;

public record StudentRequest(
        String firstname,
        String lastname,
        String emailAddress) {
}

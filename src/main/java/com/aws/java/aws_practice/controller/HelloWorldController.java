package com.aws.java.aws_practice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value   = "/api/v1/jay")
public class HelloWorldController {
	
	@GetMapping("/hello")
	public String helloWorld() {
		return  "Hi there!! Welcome to AWS.";
	}
	
	

}

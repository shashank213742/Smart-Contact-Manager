package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/")
	public String home(Model m) {
		
		m.addAttribute("title","Home - Smart Contact Manager");
		return"home";
	}
	@GetMapping("/about")
	public String about(Model m) {
		
		m.addAttribute("title","About - Smart Contact Manager");
		return"about";
	}
	@GetMapping("/signup")
	public String signup(Model m) {
		
		m.addAttribute("title","Register - Smart Contact Manager");
		m.addAttribute("user",new User());
		return"signup";
	}
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user,BindingResult result,@RequestParam(defaultValue="false") boolean agreement,Model m,HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("Please agree terms and conditions");
				throw new Exception("Please agree terms and conditions");
			}
			if(result.hasErrors()) {
				m.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setStatus(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			System.out.println(agreement);
			System.out.println(user);
			userRepository.save(user);
			m.addAttribute("user",new User());
			session.setAttribute("message",new Message("Successfully Registered", "alert-success"));
			
			return "signup";
		}catch(Exception e){
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message",new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
			
			return "signup";}
		}
	@GetMapping("/signin")
	public String login(Model m){
		m.addAttribute("title","Login Page");
		return "login";
	}

}

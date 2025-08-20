package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.contact.dao.UserRepository;
import com.contact.entities.User;
import com.contact.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

//user:buddy69 email:buddy@gmail.com pass-12345

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/*
	 * 
	 * @GetMapping("/tests")
	 * 
	 * @ResponseBody public String test() { User user = new User();
	 * user.setName("Prem Kumar"); user.setEmail("xyz@abc.com");
	 * 
	 * userRepository.save(user); return "Working.."; }
	 */

	@RequestMapping("/")
	public String handler(Model m) {
		m.addAttribute("title", "Home- Smart Contact Manager");
		System.out.println("Home page.");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About- Smart Contact Manager");
		System.out.println("about page.");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register- Smart Contact Manager");
		System.out.println("signup page.");
		m.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/do_register")
	public String signupUser(@Valid @ModelAttribute("user") User user, BindingResult resu,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You have not agreed to terms and conditions");
				throw new Exception("hasnt agreed to terms and conditions.");
			}

			if (resu.hasErrors()) {
				System.out.println("Error: " + resu.toString());
				m.addAttribute("user", user);
				return "register";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			// user.setImageURL("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("USER :" + user);
			User result = this.userRepository.save(user);

			System.out.println("\nResult : " + result);
			m.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
			return "register";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong\n" + e.getMessage(), "alert-danger"));
			return "register";
		}
	}

	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model m) {
		System.out.println("Custom Login page.");
		m.addAttribute("title", "Login Page");
		return "login";
	}

}

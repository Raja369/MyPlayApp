package org.myplay.configure;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class URLMapper {
	@GetMapping("/")
	public String indexPage() {
		return "index";
	}
	@GetMapping("/login")
	public String loginPage() {
		return "login.html";
	}
	@GetMapping("/profile")
	public String profile() {
		return "profile.html";
	}
	@GetMapping("/register")
	public String registerPage() {
		return "register.html";
	}
	@GetMapping("/results")
	public String searchPage() {
		return "search.html";
	}
	@GetMapping("/MyMusic")
	public String dashboard() {
		return "dashboard.html";
	}
}

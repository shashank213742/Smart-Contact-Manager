package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repositories.ContactRepository;
import com.smart.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@ModelAttribute
	public void addCommondata(Model m,Principal principal) {
        String userName=principal.getName();
		
		User user=userRepository.getUserByName(userName);
		m.addAttribute("user",user);
		
		
	}
	@GetMapping("/index")
	public String dashboard(Model m,Principal principal) {
		
		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
		
	}
	
	//add-contact handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("proimage") MultipartFile file ,
			Principal principal,HttpSession session) {
		
		try {
			
			String name=principal.getName();
			User user=this.userRepository.getUserByName(name);
			contact.setUser(user);
			if(file.isEmpty()) {
				System.out.println("File is empty");
				contact.setProfileimage("contact.png");
			
			
				
			}else {
				
				contact.setProfileimage(file.getOriginalFilename());
				File savefile=new ClassPathResource("static/image").getFile();
				Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			   System.out.println("Image Uploaded");
			}
			user.getContacts().add(contact);
			userRepository.save(user);
			System.out.println("Added to Database");
			session.setAttribute("message",new Message( "Your Contact is added","alert-success" ));
			
		}catch(Exception e) {
			
			session.setAttribute("message", new Message("Something went wrong","alert-danger"));
		}
		return "normal/add_contact";
		
	}
	@GetMapping("/show_contacts")
	public String showContact(Model m, Principal principal) {
		
		String userName=principal.getName();
		User user=userRepository.getUserByName(userName);
		//List<Contact> contacts=user.getContacts();
		List<Contact> contacts=contactRepository.findContactByUser(user.getId());
		m.addAttribute("contacts", contacts);
		m.addAttribute("title","Show Contacts");
		return "normal/show_contacts";
	}
	
		@GetMapping("/delete/{id}")
	public String deleteContact( @PathVariable int id ,HttpSession session,Principal principal) {
			
			Contact contact=contactRepository.findById(id).get();
			
			User user=userRepository.getUserByName(principal.getName());
			user.getContacts().remove(contact);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact deleted successfully","alert-success"));
		return"redirect:/user/show_contacts";
	}
	@PostMapping("/update/{id}")
	public String updateContact(@PathVariable int id , Model m) {
			
		m.addAttribute("title","Update Contact");
		Contact contact=contactRepository.findById(id).get();
		m.addAttribute("contact", contact);
		
		  return "normal/update_form";
	
	}
	@PostMapping("/process-update")
	public String updateNewContact(@ModelAttribute Contact contact,
			@RequestParam("proimage") MultipartFile file ,
			Principal principal,HttpSession session){
try {
	
	Contact oldcontact=contactRepository.findById(contact.getCid()).get();
	if(!file.isEmpty()) {
		
		File deleteFile=new ClassPathResource("static/image").getFile();
		File file1=new File(deleteFile,oldcontact.getProfileimage());
		file1.delete();	
		
		File savefile=new ClassPathResource("static/image").getFile();
		Path path=Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
		contact.setProfileimage(file.getOriginalFilename());
		System.out.println("Image Uploaded");
		//session.setAttribute("message",new Message( "Your Contact is Updated","alert-success" ));
		
	}else {
		contact.setProfileimage(oldcontact.getProfileimage());
		}
	User user=userRepository.getUserByName(principal.getName());
    contact.setUser(user);
    contactRepository.save(contact);
     System.out.println(contact.getProfileimage());
}
catch(Exception e) {
		e.printStackTrace();
		//session.setAttribute("message", new Message("Something went wrong","alert-danger"));
	}	
     
		return "redirect:/user/show_contacts";
		}
	@GetMapping("/profile")
	public String showProfile(Model m){
	m.addAttribute("title","Your Profile");
	return "normal/profile";
}
	@GetMapping("/settings")
	public String openSettings(Model m)
	
	{
		m.addAttribute("title","Settings");
		return "normal/settings";
	}
	
	@PostMapping("change_pass")
	public String changePassword(@RequestParam("oldpass") String oldpassword,@RequestParam("newpass") String newpassword,Principal principal,HttpSession session) {
		
	User currentUser=userRepository.getUserByName(principal.getName());
	   if(bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword())) {
		   
		   currentUser.setPassword(bCryptPasswordEncoder.encode(newpassword));
		   userRepository.save(currentUser);
		   session.setAttribute("message",new Message("Your Password is changed..","alert-success"));
	   }
	   else {
			 session.setAttribute("message",new Message("Please Enter Correct Password", "alert-danger"));
			 return "redirect:/user/settings";
	   }
		return "redirect:/user/index";
	    
	}
	
	}

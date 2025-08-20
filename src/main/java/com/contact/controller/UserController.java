package com.contact.controller;

import java.io.File;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Template.Session;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.contact.dao.ContactRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Contacts;
import com.contact.entities.User;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserRepository userRepository;
	@Autowired
	public ContactRepository contactRepository;

	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model, Principal principal) {
		model.addAttribute("title", "Profile");
		String name = principal.getName(); // Get the username of the logged-in user
		User user = this.userRepository.getUserbyUsername(name); // Fetch the user details from the database
		System.out.println("USER : \n " + user);
		model.addAttribute("user", user);
		System.out.println("On user profile page");
		return "normal/profile"; // This will return the view name to be resolved by the view resolver
	}

	@RequestMapping("/dash")
	public String dashboard(Model model, Principal principal) {
		// Add the user details to the model
		String name = principal.getName(); // This will get the username of the logged-in user
		System.out.println("Username: " + name);
		model.addAttribute("title", "User Dashboard");
		User user = userRepository.getUserbyUsername(name);
		System.out.println("USER : \n " + user);

		// This will return the view name to be resolved by the view resolver
		// The view resolver will look for a template named "normal/user_dashboard.html"
		// in the templates directory.

		// The URL format will be - https://localhost:6969/user/dash

		model.addAttribute("user", user);

		System.out.println("On user dashboard");
		return "normal/user_dashboard";
	}

	// add form controller
	@GetMapping("/add-contact")
	public String addContact(Model model, Principal principal) {
		String name = principal.getName();
		User user = this.userRepository.getUserbyUsername(name);
		model.addAttribute("user", user);
		System.out.println("On add contact page");

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contacts()); // Assuming Contact is an entity class for contacts
		return "normal/add_contact_form";

	}

	@PostMapping("/process-contact")
	public String processContact(
			@Valid @ModelAttribute("contact") Contacts contact,
			BindingResult result, // BindingResult to capture validation errors
			@RequestParam(value = "profileImage", required = false) MultipartFile file,
			Principal principal,
			Model model,
			HttpSession session) {
		try {

			if (file.isEmpty() || file == null) {
				// If the file is empty then we return our message
				System.out.println("File is empty, not uploading any image."); // Set a default image if no file is
				// contact.setCimage("/static/img/default.png"); // Set a default image name in
				// the contact entity
				contact.setCimage("contact.png"); // Set a default image name in the contact entity

			} else {
				// add file to the folder and set the contact image
				contact.setCimage(file.getOriginalFilename()); // Set the image name in the contact entity
				System.out.println("File is not empty, uploading image: " + file.getOriginalFilename());

				User user = this.userRepository.getUserbyUsername(principal.getName());
				String path = new File("src/main/resources/static/img").getAbsolutePath();
				File saveFile = new File(path); // Ensure the directory exists
				if (!saveFile.exists())
					saveFile.mkdirs();

				String uniqueFileName = user.getId() + "_" + file.getOriginalFilename();
				Path filePath = Paths.get(saveFile.getAbsolutePath(), uniqueFileName);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				contact.setCimage(uniqueFileName);
				System.out.println("Image uploaded successfully: " + filePath.toString());
			}
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
				model.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}

			String name = principal.getName();
			User user = this.userRepository.getUserbyUsername(name);

			contact.setUser(user); // Set the user for the contact
			user.getContacts().add(contact); // Add the contact to the user's contacts list
			this.userRepository.save(user); // Save the user, which will also save the contact due to cascading
			System.out.println("Contact added: " + contact.getName() + " for user: " + user.getEmail());
			System.out.println("\n\nADDED TO DATABASE");

			// successs message
			session.setAttribute("message", new Message("Contact added successfully!", "Success"));

			System.out.println("DATA\n" + contact.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRORS : \n" + e.getMessage());

			// error message
			session.setAttribute("message", new Message("Something went wrong while adding the contact!", "danger"));

			return "normal/add_contact_form"; // Return to the form if there's an error
		}
		return "redirect:/user/dash"; // Redirect to the dashboard after saving
	}

	// show contacts handler
	// per page 7 contacts
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		String name = principal.getName();
		m.addAttribute("title", "Show Contacts");
		User user = this.userRepository.getUserbyUsername(name);

		PageRequest pageable = PageRequest.of(page, 7); // Create a pageable object with the current page and size

		org.springframework.data.domain.Page<Contacts> contacts = this.contactRepository.findAllbyUser(user.getId(),
				pageable);// Fetch contacts for the user with pagination
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		System.out.println("On show contacts page");
		return "normal/show_contacts";
	}

	// show particular contact details
	@GetMapping("/contacts/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		String name = principal.getName();
		User user = this.userRepository.getUserbyUsername(name);
		Optional<Contacts> contactOptional = this.contactRepository.findById(cid);
		Contacts contact = contactOptional.get();

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		} else {
			model.addAttribute("message", new Message("You are not authorized to view this contact!", "danger"));
			model.addAttribute("alertClass", "alert-danger");
			System.out.println("Unauthorized access attempt to contact ID: " + cid);
			return "redirect:/user/show-contacts/0"; // Redirect to contacts list if unauthorized
		}

		model.addAttribute("title", "Contact Details");
		return "normal/contact_details";
	}

	// delete contact handler
	@GetMapping("/delete-contact/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model m, Principal principal, HttpSession session) {
		String name = principal.getName();
		User user = this.userRepository.getUserbyUsername(name);
		Optional<Contacts> contactOptional = this.contactRepository.findById(cid);
		Contacts contact = contactOptional.get();

		// check if the contact belongs to the logged-in user
		if (user.getId() == contact.getUser().getId()) {
			// delete the contact
			this.contactRepository.delete(contact);
			System.out.println("Contact deleted successfully: " + contact.getName());

			// deleting the image file from the server
			String imagePath = "src/main/resources/static/img/" + contact.getCimage();

			try {
				Path path = Paths.get(imagePath);
				Files.deleteIfExists(path);
				System.out.println("Image file deleted successfully: " + imagePath);
			}

			catch (Exception e) {
				System.out.println("Error deleting image file: " + e.getMessage());
			}

			m.addAttribute("message", new Message("Contact deleted successfully!", "success"));
			session.setAttribute("message", new Message("Contact deleted successfully!", "success"));

		} else {
			m.addAttribute("message", new Message("You are not authorized to delete this contact!", "danger"));
			System.out.println("Unauthorized deletion attempt for contact ID: " + cid);
		}

		return "redirect:/user/show-contacts/0"; // Redirect to contacts list after deletion

	}

	// update contact handler

	@PostMapping("/update-contact/{cid}")
	public String updateForm(Model model, @PathVariable("cid") Integer cid, Principal principal) {

		String name = principal.getName();

		User user = this.userRepository.getUserbyUsername(name);

		Optional<Contacts> contactOptional = this.contactRepository.findById(cid);
		Contacts contact = contactOptional.get();
		model.addAttribute("title", "Update Contact");
		model.addAttribute("contact", contact); // Add the contact to the model for the update
		return "normal/update_contact_form"; // This should return the update form view
	}

	// processing updates handler
	@PostMapping("/process-update")
	public String processUpdate(@Valid @ModelAttribute("contact") Contacts contact, BindingResult result,
			@RequestParam(value = "image", required = false) MultipartFile file, Principal principal,
			HttpSession session, Model model) {
		try {
			// Fetch the old contact details to compare and update
			Contacts oldcontactdetail = this.contactRepository.findById(contact.getCid()).get();
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
				model.addAttribute("contact", contact);
				return "normal/update_contact_form";
			}

			if (file.isEmpty() || file == null) {
				System.out.println("File is empty, not uploading any image.");
				contact.setCimage(oldcontactdetail.getCimage()); // Keep the old image if no new file is uploaded
			} else {

				// delete image file from the server
				String oldImagePath = "src/main/resources/static/img/" + oldcontactdetail.getCimage();
				try {
					Path oldPath = Paths.get(oldImagePath);
					Files.deleteIfExists(oldPath);
					System.out.println("Old image file deleted successfully: " + oldImagePath);
				} catch (Exception e) {
					System.out.println("Error deleting old image file: " + e.getMessage());
				}

				// add file to the folder and set the contact image
				contact.setCimage(file.getOriginalFilename());
				System.out.println("File is not empty, uploading image: " + file.getOriginalFilename());

				User user = this.userRepository.getUserbyUsername(principal.getName());
				String path = new File("src/main/resources/static/img").getAbsolutePath();
				File saveFile = new File(path);
				if (!saveFile.exists())
					saveFile.mkdirs();

				String uniqueFileName = user.getId() + "_" + file.getOriginalFilename();
				Path filePath = Paths.get(saveFile.getAbsolutePath(), uniqueFileName);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				contact.setCimage(uniqueFileName);
				System.out.println("Image uploaded successfully: " + filePath.toString());
			}

			User user = this.userRepository.getUserbyUsername(principal.getName());
			contact.setUser(user); // Set the user for the contact
			this.contactRepository.save(contact); // Save the updated contact
			session.setAttribute("message", new Message("Contact updated successfully!", "success"));
			System.out.println("Contact updated: " + contact.getName());

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong while updating the contact!", "danger"));
			return "normal/update_contact_form"; // Return to the form if there's an error
		}
		return "redirect:/user/contacts/" + contact.getCid(); // Redirect to contacts list after updating
	}
}
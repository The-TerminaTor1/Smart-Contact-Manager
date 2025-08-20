package com.contact.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	
	@NotBlank(message = "Name field must be filled.")
	@Size(min = 3 , max = 20 , message = "Please enter atleast 3 and maximum 20 characters")
	private String name;
	
	@Column(unique = true)
	@NotBlank(message = "Email field must be filled.")
	@Email(regexp = ".*?@?[^@]*\\.+.*")
	private String email;
	
	
	@NotBlank(message = "Password field must be filled.")
	private String password;
	
	private String role;
	
	private boolean enabled;
	
	private String imageURL;
	
	@Column(length = 210)
	@NotBlank(message = "About field must be filled.")
	private String about;

	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	//here it will make one more table for contacts of a particular user and use it for mapping the data in other table
	// {mappedBy = "user"} is used so that it doesnt make another table for the foreign key management between user and contacts
	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY ,  mappedBy = "user")
	private List<Contacts> contacts = new ArrayList<>();

	public List<Contacts> getContacts() {
		return contacts;
	}

	
	public void setContacts(List<Contacts> contacts) {
		this.contacts = contacts;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getImageURL() {
		return imageURL;
	}


	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}


	public String getAbout() {
		return about;
	}


	public void setAbout(String about) {
		this.about = about;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageURL=" + imageURL + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}
	
	
	
}
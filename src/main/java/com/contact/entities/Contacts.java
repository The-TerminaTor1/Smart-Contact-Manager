package com.contact.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Contacts")
public class Contacts {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;

	@Column(length = 21)
	private String name;

	@Column(length = 21)
	private String secondName;

	@Column(length = 50)
	private String work;

	@Email(regexp = ".*?@?[^@]*\\.+.*")
	private String email;

	@Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
	@Digits(integer = 15, fraction = 0, message = "Phone number must be a valid number")
	private String phone;

	private String cimage;

	@Column(length = 256)
	private String description;

	@ManyToOne
	private User user;

	// Getters and Setters
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCimage() {
		return cimage;
	}

	public void setCimage(String cimage) {
		this.cimage = cimage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// @Override
	// public String toString() {
	// return "{" +
	// " cid='" + getCid() + "'" +
	// ", name='" + getName() + "'" +
	// ", secondName='" + getSecondName() + "'" +
	// ", work='" + getWork() + "'" +
	// ", email='" + getEmail() + "'" +
	// ", phone='" + getPhone() + "'" +
	// ", cimage='" + getCimage() + "'" +
	// ", description='" + getDescription() + "'" +
	// ", user='" + getUser() + "'" +
	// "}";

}
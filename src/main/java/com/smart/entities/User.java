package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
@Entity
@Table(name="USER")
public class User {
	@Override
	public String toString() {
		return "User [Id=" + Id + ", Name=" + Name + ", Email=" + Email + ", Password=" + Password + ", About=" + About
				+ ", Status=" + Status + ", Role=" + Role + ", ImageUrl=" + ImageUrl + ", contacts=" + contacts + "]";
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	@javax.validation.constraints.NotBlank(message="Name cannot be empty")
	@Size(min=3,max=12,message="Name between 3-18 character!!")
	private String Name;
	@Column(unique = true)
	private String Email;
	private String Password;
	@Column(length=500)
	private String About;
	private boolean Status;
	private String Role;
	private String ImageUrl;
	
	@OneToMany(mappedBy="user",cascade=CascadeType.ALL ,orphanRemoval = true,fetch = FetchType.LAZY)
	private List<Contact> contacts=new ArrayList<>();
	
	

	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(int id, String name, String email, String password, String about, boolean status, String role,
			String imageUrl) {
		super();
		Id = id;
		Name = name;
		Email = email;
		Password = password;
		About = about;
		Status = status;
		Role = role;
		ImageUrl = imageUrl;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getAbout() {
		return About;
	}
	public void setAbout(String about) {
		About = about;
	}
	public boolean isStatus() {
		return Status;
	}
	public void setStatus(boolean status) {
		Status = status;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	
	
}

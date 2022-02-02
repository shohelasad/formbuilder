package com.formreleaf.domain;

import javax.persistence.*;

@Entity
public class UserRole extends BaseEntity<Integer>{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Enumerated
	private Role role;
	
	public UserRole() {
		
	}
	
	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}



	@Override
	public Integer getId() {
		return id;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public Role getRole() {
		return role;
	}



	public void setRole(Role role) {
		this.role = role;
	}



	public void setId(Integer id) {
		this.id = id;
	}

	
	
}

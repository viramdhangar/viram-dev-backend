package com.viram.dev.dto.mat;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viram.dev.dto.DAOUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String profileFor;
	private String firstName;
	private String lastName;
	private String gender;
	private String maritalStatus;
	private String height;
	@Temporal(TemporalType.DATE)
	private Date dob;
	private String phone;
	private String email;
	private String village;
	private String tehsil;
	private String district;
	private String state;
	private Date created;
	private Date updated;

	@Transient
	private String decodedBase64;
	@Column(name = "name")
	private String name;
	@Column(name = "type")
	private String type;
	// image bytes can have large lengths so we specify a value
	// which is more than the default length for picByte column
	@Lob
	@Column(name = "picByte", length = 1000)
	private byte[] picByte;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private DAOUser user;

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}
}

package com.viram.dev.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "payment_audit")
public class PaymentResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String paymentMode;
	private String orderId;
	private String txTime;
	private String referenceId;
	private String type;
	private String txMsg;
	private String signature;
	private String orderAmount;
	private String txStatus;

	private Date created;

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private DAOUser user;
}

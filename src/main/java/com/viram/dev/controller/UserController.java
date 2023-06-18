package com.viram.dev.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.viram.dev.dto.AppVersion;
import com.viram.dev.dto.Authorities;
import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.EmailParam;
import com.viram.dev.dto.MailResponse;
import com.viram.dev.repository.AppVersionRepository;
import com.viram.dev.repository.AuthoritiesRepository;
import com.viram.dev.repository.UserRepository;
import com.viram.dev.service.EmailService;

@CrossOrigin(origins = { "*" }, maxAge = 3600)
@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Autowired
	AuthoritiesRepository authoritiesRepository;

	@Autowired
	private AppVersionRepository appVersionRepository;

	@Autowired
	private EmailService emailService;

	@GetMapping("/userById/{id}")
	public DAOUser userById(@PathVariable Long id) {

		DAOUser userDTO = new DAOUser();
		Optional<DAOUser> user = userRepository.findById(id);
		if (user.get() != null) {
			userDTO = user.get();
		}
		List<Authorities> authorities = authoritiesRepository.findAllById(userDTO.getId());
		userDTO.setAuthorities(authorities);
		return userDTO;
	}

	@GetMapping("/appVersion")
	public Iterable<AppVersion> appVersion() {
		return appVersionRepository.findAll();
	}

	@PostMapping("/appVersion")
	public AppVersion appVersion(@RequestBody AppVersion appVersion) {
		return appVersionRepository.save(appVersion);
	}

	@PostMapping("/addRemoveRole")
	public Iterable<Authorities> assignRole(@Validated @RequestBody List<Authorities> authorities)
			throws AddressException, MessagingException, IOException {
		List<Authorities> list = null;
		if (!authorities.isEmpty()) {
			DAOUser userss = userRepository.findByUsername(authorities.get(0).getUsername());
			list = authoritiesRepository.findByUser(userss);
			for (Authorities at : list) {
				authoritiesRepository.deleteById(at.getId());
			}
		}
		DAOUser user = userRepository.findByUsername(authorities.get(0).getUsername());
		for (Authorities auth : authorities) {
			auth.setUser(user);
		}
		EmailParam emailParam = new EmailParam();
		emailParam.setEmail(user.getEmail());
		emailParam.setEmailBody("Hi " + user.getFirstName()
				+ "  Your access has been modified by super admin, please reply back if you have any concerns.");
		emailParam.setSubject("Access Modification");
		emailService.sendmailOTP(emailParam);
		return authoritiesRepository.saveAll(authorities);
	}

	@GetMapping("/userByUsername/{username}")
	public DAOUser userByUsername(@PathVariable String username) {
		DAOUser user = userRepository.findByUsername(username);
		if (user != null) {
			if (user.getPicByte() != null) {
				String decodedString = new String(decompressBytes(user.getPicByte()));
				user.setDecodedBase64(decodedString);
				user.setPicByte(null);
			}
			List<Authorities> auth = authoritiesRepository.findByUserId(user.getId());
			user.setAuthorities(auth);
		}
		return user;
	}

	@GetMapping("/searchUser/{searchParam}")
	public List<DAOUser> searchUser(@PathVariable String searchParam) {
		List<DAOUser> userFirstName = userRepository.findByFirstNameStartsWith(searchParam);
		List<DAOUser> userLastName = userRepository.findByLastNameStartsWith(searchParam);
		userFirstName.addAll(userLastName);
		for (DAOUser user : userFirstName) {
			user.setAuthorities(authoritiesRepository.findByUser(user));
		}
		return userFirstName;
	}

	@PostMapping("/resetPassword")
	public DAOUser resetPassword(@RequestBody DAOUser userDTO) {
		DAOUser userDBDTO = userRepository.findByEmail(userDTO.getEmail());
		userDBDTO.setPassword(bcryptEncoder.encode(userDTO.getPassword()));
		return userRepository.save(userDBDTO);
	}

	@PostMapping("/changePassword")
	public MailResponse changePassword(@RequestBody DAOUser userDTO) {
		DAOUser userDBDTO = userRepository.findByEmail(userDTO.getEmail());
		if (bcryptEncoder.matches(userDTO.getPassword(), userDBDTO.getPassword())) {
			userDBDTO.setPassword(bcryptEncoder.encode(userDTO.getChangePassword()));
			userRepository.save(userDBDTO);
			return new MailResponse("Password updated successfully.", HttpStatus.OK);
		} else {
			return new MailResponse("Old and new password does not match.", HttpStatus.BAD_REQUEST);
		}

	}

	// uncompress the image bytes before returning it to the angular application
	public static byte[] decompressBytes(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[10000];
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (IOException ioe) {
		} catch (DataFormatException e) {
		}
		return outputStream.toByteArray();
	}

	@GetMapping(value = "/birthdays")
	public List<DAOUser> birthdays() throws ParseException {
		String pattern = "MMdd";
		DateFormat df = new SimpleDateFormat(pattern);
		Date today = Calendar.getInstance().getTime();
		String todayAsString = df.format(today);
		List<DAOUser> userList = userRepository.findAllByDateMonth(todayAsString);
		userList.forEach(user -> {
			if (user.getPicByte() != null) {
				String decodedString = new String(decompressBytes(user.getPicByte()));
				user.setDecodedBase64(decodedString);
				user.setPicByte(null);
			}
		});
		return userList;
	}
}

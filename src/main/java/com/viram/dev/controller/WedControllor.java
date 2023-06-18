package com.viram.dev.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.viram.dev.dto.mat.OtherInfo;
import com.viram.dev.dto.mat.PersonalInfo;
import com.viram.dev.dto.mat.ProfessionalInfo;
import com.viram.dev.repository.OtherInfoRepository;
import com.viram.dev.repository.PersonalInfoRepository;
import com.viram.dev.repository.ProfessionalInfoRepository;
import com.viram.dev.repository.UserRepository;

@RestController
@CrossOrigin(origins = "*")
public class WedControllor {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PersonalInfoRepository personalRepostory;

	@Autowired
	private ProfessionalInfoRepository professionalRepostory;

	@Autowired
	private OtherInfoRepository otherRepository;

	@Autowired
	private CacheManager cacheManager; // autowire cache manager
	// @Scheduled(cron = "0 0/30 * * * ?") // execure after every 30 min
	// @GetMapping("/clear-cache")

	public void clearCacheSchedule() {
		for (String name : cacheManager.getCacheNames()) {
			cacheManager.getCache(name).clear(); // clear cache by name
		}
	}

	@GetMapping("/personalInfo/{userId}")
	public Optional<Object> personalInfo(@Validated @PathVariable Long userId) {
		return userRepository.findById(userId).map(user -> {
			List<PersonalInfo> personalInfoList = personalRepostory.findAllByUser(user);
			for (PersonalInfo personalUser : personalInfoList) {
				if (personalUser.getPicByte() != null) {
					String decodedString = new String(decompressBytes(personalUser.getPicByte()));
					personalUser.setDecodedBase64(decodedString);
					personalUser.setPicByte(null);
				}
			}
			return personalInfoList;
		});
	}

	@GetMapping("/personalInfo/{email}")
	public PersonalInfo personalInfo(@Validated @PathVariable String email) {
		PersonalInfo personalUser = personalRepostory.findByEmail(email);
		if (personalUser.getPicByte() != null) {
			String decodedString = new String(decompressBytes(personalUser.getPicByte()));
			personalUser.setDecodedBase64(decodedString);
			personalUser.setPicByte(null);
		}
		return personalUser;
	}

	@PostMapping("/personalInfo/{userId}")
	public Optional<Object> personalInfo(@Validated @PathVariable Long userId, @RequestBody PersonalInfo personalInfo) {
		return userRepository.findById(userId).map(user -> {
			personalInfo.setUser(user);
			if (personalInfo.getDecodedBase64() != null) {
				String[] str1 = personalInfo.getDecodedBase64().split(",");
				String contentType = str1[0].replace(";base64", "").replace("data:", "");
				String encodedString = Base64.getEncoder().encodeToString(personalInfo.getDecodedBase64().getBytes());
				byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
				personalInfo.setName("base64");
				personalInfo.setType(contentType);
				personalInfo.setPicByte(compressBytes(decodedBytes));
			}
			PersonalInfo personalInfoSaved = personalRepostory.save(personalInfo);
			if (personalInfoSaved.getId() != null) {
				return true;
			} else {
				return false;
			}
		});
	}

	@GetMapping("/professionalInfo/{personalId}")
	public Optional<ProfessionalInfo> professionalInfo(@Validated @PathVariable Long personalId) {
		return professionalRepostory.findById(personalId);
	}

	@PostMapping("/professionalInfo/{personalId}")
	public Optional<Object> professionalInfo(@Validated @PathVariable Long personalId,
			@RequestBody ProfessionalInfo professionalInfo) {
		return personalRepostory.findById(personalId).map(personalInfo -> {
			professionalInfo.setPersonalInfo(personalInfo);
			return professionalRepostory.save(professionalInfo);
		});
	}

	@GetMapping("/otherInfo/{personalId}")
	public Optional<OtherInfo> otherInfo(@Validated @PathVariable Long personalId) {
		return otherRepository.findById(personalId);
	}

	@PostMapping("/otherInfo/{personalId}")
	public Optional<Object> otherInfo(@Validated @PathVariable Long personalId, @RequestBody OtherInfo otherInfo) {
		return personalRepostory.findById(personalId).map(personalInfo -> {
			otherInfo.setPersonalInfo(personalInfo);
			return otherRepository.save(otherInfo);
		});
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

	// compress the image bytes before storing it in the database
	public static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[10000];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

		return outputStream.toByteArray();
	}

}

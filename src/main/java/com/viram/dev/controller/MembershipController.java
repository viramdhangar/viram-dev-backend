package com.viram.dev.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.viram.dev.dto.CandidatePost;
import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.EmailParam;
import com.viram.dev.dto.MemberOnPost;
import com.viram.dev.dto.Membership;
import com.viram.dev.repository.CandidatePostRepository;
import com.viram.dev.repository.MemberOnPostRepository;
import com.viram.dev.repository.MembershipRepository;
import com.viram.dev.repository.UserRepository;
import com.viram.dev.service.EmailService;
import com.viram.dev.util.FileDownload;

@CrossOrigin(origins = { "*" }, maxAge = 3600)
@RestController
public class MembershipController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MembershipRepository membersRepository;

	@Autowired
	private MemberOnPostRepository memberOnPostRepository;

	@Autowired
	private CandidatePostRepository candidatePostRepository;

	@Autowired
	private EmailService emailService;

	
	@RequestMapping(value = "/download", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> joinedTeams() throws MalformedURLException, IOException, DocumentException {

		List<Membership> mem = this.membership(1L);
        ByteArrayInputStream bis = FileDownload.joinedTeams(mem);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ContestPlayers.pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
	
	@GetMapping("/membership/{userId}")
	public List<Membership> membership(@PathVariable Long userId) {
		return membersRepository.findAllByUserId(userId);
	}

	@Cacheable(value = "memberOnPost")
	@GetMapping("/membership/memberOnPost/{offset}/{limit}")
	public List<MemberOnPost> memberOnPost(@Validated @PathVariable(name = "offset", required = false) int offset,
			@PathVariable(name = "limit", required = false) int limit) {
		List<MemberOnPost> membersOnPost = memberOnPostRepository.findAllMemberOnPostByLimit(offset, limit);
		membersOnPost.forEach(img -> {
			if (img.getUser().getPicByte() != null) {
				img.getUser().setDecodedBase64(new String(decompressBytes(img.getUser().getPicByte())));
				img.getUser().setPicByte(null);
			}
		});
		return membersOnPost;
	}

	@Cacheable(value = "/membership/candidatePost")
	@GetMapping("/membership/candidatePost")
	public Iterable<CandidatePost> memberOnPost() {
		return candidatePostRepository.findAll();
	}

	@CacheEvict(cacheNames = { "memberOnPost" }, allEntries = true)
	@PostMapping("/membership/memberOnPost")
	public MemberOnPost memberOnPost(@Validated @RequestBody MemberOnPost memberOnPost) {
		Optional<CandidatePost> cp = candidatePostRepository.findById(memberOnPost.getAppointId());
		if (cp.isPresent()) {
			memberOnPost.setApoint(cp.get());
		}
		Optional<DAOUser> user = userRepository.findById(memberOnPost.getSelectedUserId());
		if (user.isPresent()) {
			memberOnPost.setUser(user.get());
		}
		return memberOnPostRepository.save(memberOnPost);
	}

	@CacheEvict(cacheNames = { "memberOnPost" }, allEntries = true)
	@DeleteMapping("/membership/memberOnPost/{appointedId}")
	public boolean deleteMemberOnPost(@PathVariable Long appointedId) {
		Optional<MemberOnPost> cp = memberOnPostRepository.findById(appointedId);
		if (cp.isPresent()) {
			memberOnPostRepository.delete(cp.get());
			return true;
		}
		return false;
	}

	@Cacheable(value = "premiumMembers")
	@GetMapping("/membership/{offset}/{limit}")
	public List<Membership> membership(@Validated @PathVariable(name = "offset", required = false) int offset,
			@PathVariable(name = "limit", required = false) int limit) {
		List<Membership> membershipList = membersRepository.findAllMembershipByLimit(offset, limit);
		membershipList.forEach(membership -> {
			try {
				String decodedString = new String(decompressBytes(membership.getUser().getPicByte()));
				membership.getUser().setDecodedBase64(decodedString);
				membership.getUser().setPicByte(null);
			} catch (Exception e) {
				System.out.println("image currupted");
			}
		});
		return membershipList;
	}

	@CacheEvict(cacheNames = { "premiumMembers" }, allEntries = true)
	@PostMapping("/membership/{userId}")
	public Optional<Object> saveMembership(@PathVariable(value = "userId") Long userId,
			@Validated @RequestBody Membership membership) {
		return userRepository.findById(userId).map(user -> {
			membership.setUser(user);
			/*
			 * if(membership.getDecodedBase64() != null) { String[] str1 =
			 * membership.getDecodedBase64().split(","); String contentType =
			 * str1[0].replace(";base64", "").replace("data:", ""); String encodedString =
			 * Base64.getEncoder().encodeToString(membership.getDecodedBase64().getBytes());
			 * byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
			 * membership.setName("base64"); membership.setType(contentType);
			 * membership.setPicByte(compressBytes(decodedBytes)); }
			 */
			Membership membershipResponse = membersRepository.save(membership);
			EmailParam emailParam = new EmailParam();
			emailParam.setEmail(user.getEmail());
			emailParam.setEmailBody("Hi " + user.getFirstName()
					+ "  You have taken membership successfully, Please check the status on the app.");
			emailParam.setSubject("Membership taken");
			try {
				emailService.sendmailOTP(emailParam);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (membershipResponse.getUser().getPicByte() != null) {
				String decodedString = new String(decompressBytes(membershipResponse.getUser().getPicByte()));
				membershipResponse.getUser().setDecodedBase64(decodedString);
				membershipResponse.getUser().setPicByte(null);
			}
			return membershipResponse;
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

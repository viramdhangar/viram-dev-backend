package com.viram.dev.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.itextpdf.text.Image;
import com.viram.dev.dto.Authorities;
import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.EmailParam;
import com.viram.dev.dto.Post;
import com.viram.dev.dto.UserComment;
import com.viram.dev.dto.UserLike;
import com.viram.dev.dto.UserPost;
import com.viram.dev.dto.UserWish;
import com.viram.dev.repository.AuthoritiesRepository;
import com.viram.dev.repository.PostRepository;
import com.viram.dev.repository.UserCommentRepository;
import com.viram.dev.repository.UserLikeRepository;
import com.viram.dev.repository.UserRepository;
import com.viram.dev.repository.UserWishRepository;
import com.viram.dev.service.EmailService;
import com.viram.dev.util.FileDownload;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 3600)
public class PostController {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserLikeRepository userLikeRepository;

	@Autowired
	private UserCommentRepository userCommentRepository;

	@Autowired
	private UserWishRepository userWishRepository;

	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Autowired
	private EmailService emailService;

	@PostMapping(value = "/post/{userId}")
	public Optional<Object> post(@PathVariable Long userId, @RequestBody Post post) {
		return userRepository.findById(userId).map(user -> {
			Post mypost = new Post();
			if (post.getDecodedBase64() != null) {
				String[] str1 = post.getDecodedBase64().split(",");
				String contentType = str1[0].replace(";base64", "").replace("data:", "");
				String encodedString = Base64.getEncoder().encodeToString(post.getDecodedBase64().getBytes());
				byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
				mypost = new Post("base64", contentType, compressBytes(decodedBytes), post.getPostDescription());
			} else {
				mypost.setPostDescription(post.getPostDescription());
			}
			mypost.setUser(user);
			if (post.getId() != null) {
				mypost.setId(post.getId());
			}
			mypost.setStatus("Active");
			Post responsePost = postRepository.save(mypost);
			if (responsePost.getPicByte() != null) {
				String decodedString = new String(decompressBytes(responsePost.getPicByte()));
				responsePost.setDecodedBase64(decodedString);
				responsePost.setPicByte(null);
			}
			return responsePost;
		});
	}

	@DeleteMapping("/deletePost/{postId}/{profileId}")
	public Boolean deletePost(@PathVariable Long postId, @PathVariable Long profileId)
			throws AddressException, MessagingException, IOException {
		List<Authorities> authorities = authoritiesRepository.findByUserId(profileId);
		Optional<Post> post = postRepository.findById(postId);
		if (post.isPresent()) {
			post.get().setStatus("Inactive");
			postRepository.save(post.get());
			for (Authorities auth : authorities) {
				if (auth.getAuthority().equalsIgnoreCase("ROLE_SUPER")
						|| auth.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
					EmailParam emailParam = new EmailParam();
					emailParam.setEmail(post.get().getUser().getEmail());
					emailParam.setEmailBody("Hi " + post.get().getUser().getFirstName()
							+ "  Your Post was Inactivated today by admin, Please reply to this email if you want more clarification.");
					emailParam.setSubject("Post Inactivated");
					emailService.sendmailOTP(emailParam);
					break;
				}
			}
			return true;
		}
		return false;
	}

	@GetMapping(value = "/post/{offset}/{limit}")
	public List<Post> post(@PathVariable(name = "offset", required = false) int offset,
			@PathVariable(name = "limit", required = false) int limit) {
		List<Post> posts = postRepository.findAllPostByLimit(offset, limit);
		posts.forEach(img -> {
			try {
				if (img.getPicByte() != null) {
					String decodedString = new String(decompressBytes(img.getPicByte()));
					img.setDecodedBase64(decodedString);
					img.setPicByte(null);
				}

				if (img.getUser().getPicByte() != null) {
					img.getUser().setDecodedBase64(new String(decompressBytes(img.getUser().getPicByte())));
					img.getUser().setPicByte(null);
				}
			} catch (Exception e) {
				System.out.println("image currupted");
			}
		});
		return posts;
	}

	@PostMapping(value = "/like")
	public Long like(@RequestBody UserPost userPost) {
		UserLike userLike = new UserLike();
		Optional<Post> post = postRepository.findById(userPost.getPostId());
		if (post.isPresent()) {
			userLike.setPost(post.get());
		}
		Optional<DAOUser> user = userRepository.findById(userPost.getUserId());
		if (user.isPresent()) {
			userLike.setUser(user.get());
		}
		UserLike userLikeSubmitted = userLikeRepository.save(userLike);
		return userLikeSubmitted.getId();
	}

	@GetMapping(value = "/like/{postId}")
	public Optional<Object> like(@PathVariable Long postId) {
		return postRepository.findById(postId).map(post -> {
			System.out.println(userLikeRepository.findAllByPost(post));
			return userLikeRepository.findAllByPost(post);
		});
	}

	@DeleteMapping(value = "/delete/like/{likeId}")
	public void deleteLike(@PathVariable Long likeId) {
		userLikeRepository.deleteById(likeId);
	}

	@PostMapping(value = "/comment")
	public Long comment(@RequestBody UserPost userPost) {
		UserComment userComment = new UserComment();
		Optional<Post> post = postRepository.findById(userPost.getPostId());
		if (post.isPresent()) {
			userComment.setPost(post.get());
		}
		Optional<DAOUser> user = userRepository.findById(userPost.getUserId());
		if (user.isPresent()) {
			userComment.setUser(user.get());
		}
		userComment.setComment(userPost.getComment());
		UserComment userCommentSubmitted = userCommentRepository.save(userComment);
		return userCommentSubmitted.getId();
	}

	@PostMapping(value = "/birthdayWish")
	public Long birthdayWish(@RequestBody UserPost userPost) {
		UserWish userWish = new UserWish();

		Optional<DAOUser> birth = userRepository.findById(userPost.getBirthId());
		if (birth.isPresent()) {
			userWish.setBirth(birth.get());
		}
		Optional<DAOUser> user = userRepository.findById(userPost.getUserId());
		if (user.isPresent()) {
			userWish.setUser(user.get());
		}
		userWish.setComment(userPost.getComment());
		UserWish userCommentSubmitted = userWishRepository.save(userWish);
		return userCommentSubmitted.getId();
	}

	@DeleteMapping(value = "/delete/birthdayWish/{commentId}")
	public void deletebirthdayWish(@PathVariable Long commentId) {
		userWishRepository.deleteById(commentId);
	}

	@GetMapping(value = "/birthdayWishes/{birthId}")
	public Optional<Object> birthdayWishes(@PathVariable Long birthId) {
		return userRepository.findById(birthId).map(birth -> {
			return userWishRepository.findAllByBirthOrderByCreatedDesc(birth);
		});
	}

	@GetMapping(value = "/comment/{postId}")
	public Optional<Object> comment(@PathVariable Long postId) {
		return postRepository.findById(postId).map(post -> {
			return userCommentRepository.findAllByPostOrderByCreatedDesc(post);
		});
	}

	@DeleteMapping(value = "/delete/comment/{commentId}")
	public void deleteComment(@PathVariable Long commentId) {
		userCommentRepository.deleteById(commentId);
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

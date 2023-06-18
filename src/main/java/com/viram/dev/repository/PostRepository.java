package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query(value = "select * from post where status='Active' order by created desc limit ?,?", nativeQuery = true)
	public List<Post> findAllPostByLimit(int offset, int limit);

	int deleteByUser(DAOUser user);
}

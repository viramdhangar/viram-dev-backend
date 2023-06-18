package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.viram.dev.dto.MemberOnPost;

public interface MemberOnPostRepository extends CrudRepository<MemberOnPost, Long> {

	@Query(value = "select * from appointed_members order by sort_order asc limit ?,?", nativeQuery = true)
	public List<MemberOnPost> findAllMemberOnPostByLimit(int offset, int limit);
}

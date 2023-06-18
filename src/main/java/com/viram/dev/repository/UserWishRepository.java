package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.UserWish;

public interface UserWishRepository extends JpaRepository<UserWish, Long> {

	long countByBirth(DAOUser birth);

	List<UserWish> findAllByBirthOrderByCreatedDesc(DAOUser birth);

	void deleteByBirthId(Long birthId);
}

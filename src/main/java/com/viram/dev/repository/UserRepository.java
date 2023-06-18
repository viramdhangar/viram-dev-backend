package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.viram.dev.dto.DAOUser;

public interface UserRepository extends CrudRepository<DAOUser, Long> {

	DAOUser findByUsername(String username);

	DAOUser findByEmail(String email);

	List<DAOUser> findByFirstNameStartsWith(String searchParam);

	List<DAOUser> findByLastNameStartsWith(String searchParam);

	List<DAOUser> findAllByDateMonth(String date);
}

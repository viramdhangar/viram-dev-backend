package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.viram.dev.dto.DAOUser;
import com.viram.dev.dto.mat.PersonalInfo;

public interface PersonalInfoRepository extends CrudRepository<PersonalInfo, Long> {

	public PersonalInfo findByEmail(String email);

	public List<PersonalInfo> findAllByUser(DAOUser user);
}

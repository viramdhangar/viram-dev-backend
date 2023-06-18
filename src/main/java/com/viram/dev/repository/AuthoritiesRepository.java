package com.viram.dev.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.viram.dev.dto.Authorities;
import com.viram.dev.dto.DAOUser;

public interface AuthoritiesRepository extends CrudRepository<Authorities, Long> {

	public List<Authorities> findAllById(Long id);

	public List<Authorities> findByUserId(Long userId);

	public List<Authorities> findByUser(DAOUser user);

	public List<Authorities> findByUserName(String userName);

	public void deleteByUsername(String userName);

}

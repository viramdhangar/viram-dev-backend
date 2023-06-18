package com.viram.dev.repository;

import org.springframework.data.repository.CrudRepository;

import com.viram.dev.dto.AppVersion;

public interface AppVersionRepository extends CrudRepository<AppVersion, Long> {

}

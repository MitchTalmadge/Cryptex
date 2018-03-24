package com.mitchtalmadge.cryptex.service.entity;

import com.mitchtalmadge.cryptex.domain.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileEntityRepository extends CrudRepository<FileEntity, Long> {

    List<FileEntity> findByName(String name);

}

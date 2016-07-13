package com.zzkun.dao;

import com.zzkun.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by kun on 2016/7/6.
 */
public interface UserRepository extends JpaRepository<User, String> {

    @Override
    List<User> findAll();

    @Override
    long count();

    @Override
    <S extends User> S save(S entity);

    @Override
    User findOne(String s);

    @Override
    void deleteAllInBatch();

    User findByUvaId(Integer uvaid);

    @Override
    Page<User> findAll(Pageable pageable);
}

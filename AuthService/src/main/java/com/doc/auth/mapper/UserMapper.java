package com.doc.auth.mapper;

import com.doc.auth.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);
    User selectById(Long id);
    void insert(User user);
    void update(User user);
    void delete(Long id);
} 
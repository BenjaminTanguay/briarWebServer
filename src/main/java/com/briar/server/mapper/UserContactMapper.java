package com.briar.server.mapper;


import com.briar.server.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserContactMapper {

    @Select("select * from contacts")
    List<User> findAll();
}

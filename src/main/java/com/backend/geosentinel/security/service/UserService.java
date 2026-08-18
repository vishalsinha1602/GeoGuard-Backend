package com.backend.geosentinel.security.service;


import com.backend.geosentinel.security.dto.UserDto;
import com.backend.geosentinel.security.entity.User;

public interface UserService {

    User getUserById(Long id);

}

package com.backend.geosentinel.security.service;


import com.backend.geosentinel.exception.ResourceNotFoundException;
import com.backend.geosentinel.security.entity.User;
import com.backend.geosentinel.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class userServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id: "+id ));
    }

//    @Override
//    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
//        User user = getCurrentUser();
//
//
//        if (profileUpdateRequestDto.getName() != null) user.setName(profileUpdateRequestDto.getName());
//        if (profileUpdateRequestDto.getName() != null) user.setEmail(profileUpdateRequestDto.getName());
//        if (profileUpdateRequestDto.getName() != null) user.setPassword(profileUpdateRequestDto.getName());
//
//        userRepository.save(user);
//    }
//
//    @Override
//    public UserDto getMyProfile() {
//        User currentUser = getCurrentUser();
//
//        log.info("Getting the profile for user with id: {}", currentUser.getId());
//        return modelMapper.map(currentUser, UserDto.class);
//
//    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));
    }


}

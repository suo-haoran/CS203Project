package com.cs203g3.ticketing.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findByName(ERole.valueOf(name))
                .orElseThrow(() -> new ResourceNotFoundException("Role " + name +" not found: "));
    }
}

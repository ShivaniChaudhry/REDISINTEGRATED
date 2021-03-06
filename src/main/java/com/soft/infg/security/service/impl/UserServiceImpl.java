package com.soft.infg.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.soft.infg.dao.entity.Role;
import com.soft.infg.dao.entity.User;
import com.soft.infg.security.dao.RoleDao;
import com.soft.infg.security.dao.UserDao;
import com.soft.infg.security.model.RoleDto;
import com.soft.infg.security.model.UserDto;
import com.soft.infg.security.service.UserService;

import java.util.*;


@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		try {
			if(user == null){
				throw new UsernameNotFoundException("Invalid username or password.");
			}
				
		} catch (UsernameNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
	}

	private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		user.getRoles().forEach(role -> 
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
		);
		return authorities;
	}

	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(long id) {
		userDao.deleteById(id);
	}

	@Override
	public User findOne(String username) {
		User user = userDao.findByUsername(username);
		if(user==null) {
			System.out.println(username+ " is invalid");
		}
			return user;
	}

	@Override
	public User findById(long id) {
		return userDao.findById(id).get();
	}

	@Override
    public User save(UserDto user) {
	    User newUser = new User();
	    newUser.setUsername(user.getUsername());
	    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
	    newUser.setRoles(convertToEnt(user.getRole()));
        return userDao.save(newUser);
    }
	
	Set<Role> convertToEnt(Set<RoleDto> roleDto){
		Set<Role> roleEnt = new HashSet<>();
		roleDto.forEach(r->{
			Role role = roleDao.findRoleByName(r.getName());
			if(role == null)
			{
				role = new Role();
				role.setName(r.getName());				
			}
			roleEnt.add(role);
		});
		return roleEnt;
		
	}

}

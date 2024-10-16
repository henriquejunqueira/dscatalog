package com.devsuperior.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){
		
		Page<User> list = userRepository.findAll(pageable);
		
		return list.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = userRepository.findById(id);
		
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found!"));
		
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO userInsertDTO) {
		User entity = new User();
		
		copyDtoToEntity(userInsertDTO, entity);
		
		entity.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));
		entity = userRepository.save(entity);
		
		System.out.println("Entity to save: " + entity);
		
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {
		
		try {
			
			User entity = userRepository.getOne(id);
			
			copyDtoToEntity(userUpdateDTO, entity);
			
			entity = userRepository.save(entity);
			
			return new UserDTO(entity);
			
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
	}

	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(UserDTO userDTO, User entity) {
		
		entity.setFirstName(userDTO.getFirstName());
		entity.setLastName(userDTO.getLastName());
		entity.setEmail(userDTO.getEmail());
		
		entity.getRoles().clear();
		
		for(RoleDTO roleDto : userDTO.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			
			entity.getRoles().add(role);
		}
		
	}
	
}

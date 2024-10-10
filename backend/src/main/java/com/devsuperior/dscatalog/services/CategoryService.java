package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired // injeta uma dependência, nesse caso CategoryRepository
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		
		List<Category> list = categoryRepository.findAll();
		
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		
		// orElseThrow lança uma excessão ser obj vier vazio, ou seja, se o id não for encontrado
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found!"));
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDTO) {
		Category entity = new Category();
		
		entity.setName(categoryDTO.getName());
		
		entity = categoryRepository.save(entity);
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
		
		try {
			// o getOne não toca no bd, ele instancia um obj provisório com os dados e o id no obj e só quando é 
			// pedido para salvar que ele acessa o bd
			Category entity = categoryRepository.getOne(id);
			
			entity.setName(categoryDTO.getName());
			entity = categoryRepository.save(entity);
			
			return new CategoryDTO(entity);
			
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
	}
	
}

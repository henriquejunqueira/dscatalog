package com.devsuperior.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		
		Page<Product> list = productRepository.findAll(pageRequest);
		
		return list.map(x -> new ProductDTO(x));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = productRepository.findById(id);
		
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found!"));
		
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDTO) {
		Product entity = new Product();
		
		copyDtoToEntity(productDTO, entity);
		
		entity = productRepository.save(entity);
		
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDTO) {
		
		try {
			
			Product entity = productRepository.getOne(id);
			
			copyDtoToEntity(productDTO, entity);
			
			entity = productRepository.save(entity);
			
			return new ProductDTO(entity);
			
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		
	}

	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO productDTO, Product entity) {
		
		entity.setName(productDTO.getName());
		entity.setDescription(productDTO.getDescription());
		entity.setDate(productDTO.getDate());
		entity.setImgUrl(productDTO.getImgUrl());
		entity.setPrice(productDTO.getPrice());
		
		entity.getCategories().clear(); // limpa a coleção (Set) de categorias
		
		for(CategoryDTO catDto : productDTO.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			
			entity.getCategories().add(category);
		}
		
	}
	
}

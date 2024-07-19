package com.devsuperior.DSCatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.DSCatalog.dto.CategoryDTO;
import com.devsuperior.DSCatalog.entities.Category;
import com.devsuperior.DSCatalog.repositories.CategoryRepository;
import com.devsuperior.DSCatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Component
public class CategoryService {
	
	@Autowired
	CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		//Converter a lista de category para uma lista de categoryDTO
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		//Optional evita trabalhar com o valor nulo
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return new CategoryDTO(entity);
		
		
	}
 
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id,CategoryDTO dto) {
		try {
		Category entity = repository.getReferenceById(id);
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
		catch(EntityNotFoundException e){
				throw new ResourceNotFoundException("id not found" + id);
			}
		}
}

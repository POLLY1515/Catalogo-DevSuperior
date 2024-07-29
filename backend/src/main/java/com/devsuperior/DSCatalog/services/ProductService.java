package com.devsuperior.DSCatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.DSCatalog.dto.CategoryDTO;
import com.devsuperior.DSCatalog.dto.ProductDTO;
import com.devsuperior.DSCatalog.entities.Category;
import com.devsuperior.DSCatalog.entities.Product;
import com.devsuperior.DSCatalog.repositories.CategoryRepository;
import com.devsuperior.DSCatalog.repositories.ProductRepository;
import com.devsuperior.DSCatalog.services.exceptions.DatabaseException;
import com.devsuperior.DSCatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Component
public class ProductService {
	
	@Autowired
	ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged( Pageable pageable ){
		Page<Product> list = repository.findAll(pageable);
		//Converter a lista de category para uma lista de categoryDTO
		return list.map(x -> new ProductDTO(x));
		
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		//Optional evita trabalhar com o valor nulo
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return new ProductDTO(entity, entity.getCategories());
		
		
	}
 
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	

	@Transactional
	public ProductDTO update(Long id,ProductDTO dto) {
		try {
		Product entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
		catch(EntityNotFoundException e){
				throw new ResourceNotFoundException("id not found" + id);
			}
		}
		
		@Transactional(propagation = Propagation.SUPPORTS)
		public void delete(Long id) {
			if (!repository.existsById(id)) {
				throw new ResourceNotFoundException("id not found");
			}
			try {
		        	repository.deleteById(id);    		
			}
		    	catch (DataIntegrityViolationException e) {
		        	throw new DatabaseException("Integrity violation");
		   	}
		}
		
		private void copyDtoToEntity(ProductDTO dto, Product entity) {
			entity.setName(dto.getName());
			entity.setDescription(dto.getDescription());
			entity.setDate(dto.getDate());
			entity.setImgUrl(dto.getImgUrl());
			entity.setPrice(dto.getPrice());
			
			entity.getCategories().clear();
			for(CategoryDTO catDTO : dto.getCategories()) {
				Category category = categoryRepository.getOne(catDTO.getId());
				entity.getCategories().add(category);
			}
			
			
		}
	

	
}
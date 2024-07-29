package com.devsuperior.DSCatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.DSCatalog.dto.ProductDTO;
import com.devsuperior.DSCatalog.entities.Product;
import com.devsuperior.DSCatalog.repositories.ProductRepository;
import com.devsuperior.DSCatalog.services.exceptions.DatabaseException;
import com.devsuperior.DSCatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Component
public class ProductService {
	
	@Autowired
	ProductRepository repository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> list = repository.findAll(pageRequest);
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
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id,ProductDTO dto) {
		try {
		Product entity = repository.getReferenceById(id);
		//entity.setName(dto.getName());
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
		
	

	
}

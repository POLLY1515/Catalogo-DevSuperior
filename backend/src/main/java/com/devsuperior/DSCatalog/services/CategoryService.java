package com.devsuperior.DSCatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.DSCatalog.entities.Category;
import com.devsuperior.DSCatalog.repositories.CategoryRepository;

@Component
public class CategoryService {
	
	@Autowired
	CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<Category> findAll(){
		return repository.findAll();
	}
}

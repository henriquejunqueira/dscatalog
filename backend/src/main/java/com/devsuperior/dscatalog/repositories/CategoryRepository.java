package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;

@Repository // define que o componente pode ser injetado em outra classe
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

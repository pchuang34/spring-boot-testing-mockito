package com.luv2code.springmvc.repo;

import com.luv2code.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository                         //CrudRepository<Entity, Primary Key>
public interface StudentDao extends CrudRepository<CollegeStudent, Integer> {

    CollegeStudent findByEmailAddress(String e);
}

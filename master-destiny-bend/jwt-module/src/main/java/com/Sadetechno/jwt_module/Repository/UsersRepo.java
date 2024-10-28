package com.Sadetechno.jwt_module.Repository;


import com.Sadetechno.jwt_module.model.OurUsers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends MongoRepository<OurUsers, String> {

    Optional<OurUsers> findByEmail(String email);
    List<OurUsers> findAllByOrderByIdDesc();

//    @Query("SELECT u.name FROM OurUsers u WHERE u.id = :id")
    Optional<String> findUserNameById(@Param("id") String id);

}
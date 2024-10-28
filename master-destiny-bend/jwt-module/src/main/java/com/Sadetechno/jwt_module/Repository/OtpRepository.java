package com.Sadetechno.jwt_module.Repository;
import com.Sadetechno.jwt_module.model.OtpEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface OtpRepository extends MongoRepository<OtpEntity, String> {
    Optional<OtpEntity> findByEmailAndOtp(String email, String otp);
    List<OtpEntity> findAllByOrderByIdDesc();
}




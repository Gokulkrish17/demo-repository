package com.Sadetechno.comment_module.Repository;

import com.Sadetechno.comment_module.model.StatusNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<StatusNotification,Long> {
}

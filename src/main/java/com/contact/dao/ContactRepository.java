package com.contact.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.contact.entities.Contacts;

public interface ContactRepository extends JpaRepository<Contacts, Integer> {
    // This interface will automatically provide CRUD operations for Contacts entity
    // No need to write any code here, Spring Data JPA will handle it
    // You can add custom query methods if needed, for example:
    // List<Contacts> findByUser(User user)

    // List<Contacts> findByUserId(Integer userId)

    @Query("SELECT c FROM Contacts c WHERE c.user.id = :userId")
    public org.springframework.data.domain.Page<Contacts> findAllbyUser(@Param("userId") Integer userId,
            Pageable pageable);

}

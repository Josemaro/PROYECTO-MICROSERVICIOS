package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Date;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement,Long> {
    List<Movement> findByAccountId(Long id);
    //example SELECT ALL FROM TBL_MOVEMENTS  WHERE MOVEMENT_DATE >= '2022-02-01' AND MOVEMENT_DATE < '2022-03-02'
    @Query(value = "SELECT * FROM TBL_MOVEMENTS T WHERE MOVEMENT_DATE >= :startDate AND MOVEMENT_DATE < :endDate",nativeQuery = true)
    List<Movement> getAllBetweenDates(@Param("startDate") Date startDate, @Param("endDate")Date endDate);
}

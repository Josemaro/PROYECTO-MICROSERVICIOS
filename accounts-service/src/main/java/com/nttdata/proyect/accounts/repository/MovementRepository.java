package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Date;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement,Long> {
    List<Movement> findByAccountId(Long id);
    //SELECT T.* FROM TBL_MOVEMENTS T, TBL_ACCOUNTS A   WHERE T.MOVEMENT_DATE >= '2022-02-01' AND MOVEMENT_DATE <  '2022-03-01' AND A.ID = T.ACCOUNT_ID AND A.ID = 1 , ,nativeQuery = true
    @Query(value = "SELECT T.* FROM TBL_MOVEMENTS T, TBL_ACCOUNTS A  WHERE T.MOVEMENT_DATE >= :startDate AND MOVEMENT_DATE < :endDate AND A.ID = T.ACCOUNT_ID AND A.ID = :accountId",nativeQuery = true)
    List<Movement> getAllBetweenDates(@Param("accountId")int accountId,@Param("startDate") String startDate, @Param("endDate")String endDate);
}

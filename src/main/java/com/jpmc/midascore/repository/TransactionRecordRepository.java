package com.jpmc.midascore.repository;
import com.jpmc.midascore.foundation.TransacRecord;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface TransactionRecordRepository extends JpaRepository<TransacRecord, Long> {
    }


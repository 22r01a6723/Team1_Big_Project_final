package com.smarsh.compliance.repository;

import com.smarsh.compliance.entity.Flag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlagRepository extends JpaRepository<Flag, String> {
}

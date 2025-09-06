package com.Project_1.Review.repository;

import com.Project_1.Review.entity.Flag;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlagRepository extends JpaRepository<Flag, String> {

    List<Flag> findByTenantIdAndNetwork(String tenantId, String network);

    List<Flag> findByMessageIdAndTenantIdAndNetwork(String messageId, String tenantId, String network);

    List<Flag> findByFlagIdAndTenantIdAndNetwork(String flagId, String tenantId, String network);

    List<Flag> findByRuleIdAndTenantIdAndNetwork(String ruleId, String tenantId, String network);

    List<Flag> findByFlagDescriptionContainingIgnoreCaseAndTenantIdAndNetwork(String keyword, String tenantId, String network);

    List<Flag> findByCreatedAtBetweenAndTenantIdAndNetwork(Long start, Long end, String tenantId, String network);
}


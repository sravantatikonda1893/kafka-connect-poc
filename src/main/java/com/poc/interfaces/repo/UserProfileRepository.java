package com.poc.interfaces.repo;

import com.poc.interfaces.model.UserRec;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author sravantatikonda
 */
@Repository
public interface UserProfileRepository extends IBaseRepository<UserRec, Integer>,
    JpaSpecificationExecutor<UserRec> {

}

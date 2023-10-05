package com.fin.model.whitelablelling;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface WhiteLabellingRepository extends CrudRepository<WhiteLabelling, Long> {

	WhiteLabelling findByClientId(String clientId);

	void deleteByClientId(String clientId);

}

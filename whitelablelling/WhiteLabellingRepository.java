package com.fin.model.whitelablelling;

import java.util.List;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface WhiteLabellingRepository extends CrudRepository<WhiteLabelling, Long> {

	List<WhiteLabelling> findByClientId(String clientId);

	void deleteByClientId(String clientId);

	List<WhiteLabelling> findByFileTag(String fileTag);

	void deleteByFileTag(String fileTag);

	List<WhiteLabelling> findByClientIdAndFileTag(String clientId, String fileTag);

	void deleteByClientIdAndFileTag(String clientId, String fileTag);

	List<WhiteLabelling> getByClientIdAndFileTag(String clientId, String fileTag);

	void deleteAllByClientIdAndFileTag(String clientId, String fileTag);

	


//    List<WhiteLabelling> findByClientIdAndFileTag(String clientId, String fileTag);
//
//    void deleteByClientIdAndFileTag(String clientId, String fileTag);

}

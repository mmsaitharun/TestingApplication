package com.demo.application.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.demo.application.dto.AutoSignInDto;
import com.demo.application.entity.AutoSignInDo;
import com.demo.application.geotab.ResponseMessage;
import com.demo.application.util.ServicesUtil;

@Repository("autoSignInDao")
@EnableTransactionManagement
@Transactional(value="sessionFactoryTransactionManager")
public class AutoSignInDao {

private static final Logger logger = LoggerFactory.getLogger(AutoSignInDao.class);
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public Session getSession() {
		try {
			return sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			e.printStackTrace();
			return sessionFactory.openSession();
		}
	}
	
	protected AutoSignInDo importDto(AutoSignInDto fromDto) {
		AutoSignInDo entity = new AutoSignInDo();
		if (!ServicesUtil.isEmpty(fromDto.getSignInId()))
			entity.setSignInId(fromDto.getSignInId());;
		if (!ServicesUtil.isEmpty(fromDto.getDriverId()))
			entity.getAutoSignInDoPK().setDriverId(fromDto.getDriverId());
		if (!ServicesUtil.isEmpty(fromDto.getDriverName()))
			entity.setDriverName(fromDto.getDriverName());
		if (!ServicesUtil.isEmpty(fromDto.getDriverLat()))
			entity.setDriverLat(fromDto.getDriverLat());
		if (!ServicesUtil.isEmpty(fromDto.getDriverLon()))
			entity.setDriverLon(fromDto.getDriverLon());
		if (!ServicesUtil.isEmpty(fromDto.getMuwi()))
			entity.getAutoSignInDoPK().setMuwi(fromDto.getMuwi());
		if (!ServicesUtil.isEmpty(fromDto.getWellName()))
			entity.setWellName(fromDto.getWellName());
		if (!ServicesUtil.isEmpty(fromDto.getWellLat()))
			entity.setWellLat(fromDto.getWellLat());
		if (!ServicesUtil.isEmpty(fromDto.getWellLon()))
			entity.setWellLon(fromDto.getWellLon());
		if (!ServicesUtil.isEmpty(fromDto.getSignInStart()))
			entity.getAutoSignInDoPK().setSignInStart(fromDto.getSignInStart());
		if (!ServicesUtil.isEmpty(fromDto.getSignInEnd()))
			entity.setSignInEnd(fromDto.getSignInEnd());
		if (!ServicesUtil.isEmpty(fromDto.getTimeSignedIn()))
			entity.setTimeSignedIn(fromDto.getTimeSignedIn());
		if (!ServicesUtil.isEmpty(fromDto.getCrowFlyDistance()))
			entity.setCrowFlyDistance(fromDto.getCrowFlyDistance());
		if (!ServicesUtil.isEmpty(fromDto.getRoadDistance()))
			entity.setRoadDistance(fromDto.getRoadDistance());
		if (!ServicesUtil.isEmpty(fromDto.getDriverInField()))
			entity.setDriverInField(fromDto.getDriverInField());
		return entity;
	}

	protected AutoSignInDto exportDto(AutoSignInDo entity) {
		AutoSignInDto dto = new AutoSignInDto();
		if (!ServicesUtil.isEmpty(entity.getSignInId()))
			dto.setSignInId(entity.getSignInId());;
		if (!ServicesUtil.isEmpty(entity.getAutoSignInDoPK().getDriverId()))
			dto.setDriverId(entity.getAutoSignInDoPK().getDriverId());
		if (!ServicesUtil.isEmpty(entity.getDriverName()))
			dto.setDriverName(entity.getDriverName());
		if (!ServicesUtil.isEmpty(entity.getDriverLat()))
			dto.setDriverLat(entity.getDriverLat());
		if (!ServicesUtil.isEmpty(entity.getDriverLon()))
			dto.setDriverLon(entity.getDriverLon());
		if (!ServicesUtil.isEmpty(entity.getAutoSignInDoPK().getMuwi()))
			dto.setMuwi(entity.getAutoSignInDoPK().getMuwi());
		if (!ServicesUtil.isEmpty(entity.getWellName()))
			dto.setWellName(entity.getWellName());
		if (!ServicesUtil.isEmpty(entity.getWellLat()))
			dto.setWellLat(entity.getWellLat());
		if (!ServicesUtil.isEmpty(entity.getWellLon()))
			dto.setWellLon(entity.getWellLon());
		if (!ServicesUtil.isEmpty(entity.getAutoSignInDoPK().getSignInStart()))
			dto.setSignInStart(entity.getAutoSignInDoPK().getSignInStart());
		if (!ServicesUtil.isEmpty(entity.getSignInEnd()))
			dto.setSignInEnd(entity.getSignInEnd());
		if (!ServicesUtil.isEmpty(entity.getTimeSignedIn()))
			dto.setTimeSignedIn(entity.getTimeSignedIn());
		if (!ServicesUtil.isEmpty(entity.getCrowFlyDistance()))
			dto.setCrowFlyDistance(entity.getCrowFlyDistance());
		if (!ServicesUtil.isEmpty(entity.getRoadDistance()))
			dto.setRoadDistance(entity.getRoadDistance());
		if (!ServicesUtil.isEmpty(entity.getDriverInField()))
			dto.setDriverInField(entity.getDriverInField());
		return dto;
	}
	
	ResponseMessage saveOrUpdateIntoSignInTable(AutoSignInDto autoSignInDto) {
		ResponseMessage message = new ResponseMessage();
		message.setMessage("Save Or Update Failure");
		message.setStatus("FAILURE");
		message.setStatusCode("1");
		
		try {
			if(!ServicesUtil.isEmpty(autoSignInDto)) {
				this.getSession().saveOrUpdate(AutoSignInDo.class.toString(), importDto(autoSignInDto));
				message.setMessage("Save Or Update Success");
				message.setStatus("SUCCESS");
				message.setStatusCode("0");
			}
		} catch (Exception e) {
			logger.error("ERROR : [AutoSignInDao][saveOrUpdateIntoSignInTable][Exception] : "+e.getMessage());
			e.printStackTrace();
		}
		return message;
	}
}

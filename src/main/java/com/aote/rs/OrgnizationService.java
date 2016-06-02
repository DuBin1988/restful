package com.aote.rs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aote.entity.Organization;

@Path("org")
@Component
@Transactional
public class OrgnizationService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@GET
	public String insertOrgs() {
		Organization parent = new Organization();
		parent.name = "123456";
		parent.captial = new BigDecimal(5999);
		parent.birthday = new Date();
		parent.branches = new ArrayList<Organization>();

		Organization org1 = new Organization();
		org1.name = "123456";
		org1.captial = new BigDecimal(5999);
		org1.birthday = new Date();
		org1.email ="45758012@qq.com";
		
		org1.parent = parent;
		parent.branches.add(org1);
		
		Organization org2 = new Organization();
		org2.name = "123456";
		org2.captial = new BigDecimal(5999);
		org2.birthday = new Date();
		
		org2.parent = parent;
		parent.branches.add(org2);
		
		Session session = sessionFactory.getCurrentSession();
		session.save(parent);
		
		return "";
	}
}

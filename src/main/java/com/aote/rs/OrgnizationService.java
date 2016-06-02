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

import com.aote.entity.Orgnization;

@Path("org")
@Component
@Transactional
public class OrgnizationService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@GET
	public String insertOrgs() {
		Orgnization parent = new Orgnization();
		parent.name = "123456";
		parent.salary = new BigDecimal(5999);
		parent.birthday = new Date();
		parent.children = new ArrayList<Orgnization>();

		Orgnization org1 = new Orgnization();
		org1.name = "123456";
		org1.salary = new BigDecimal(5999);
		org1.birthday = new Date();
		org1.parent = parent;

		Orgnization org2 = new Orgnization();
		org2.name = "123456";
		org2.salary = new BigDecimal(5999);
		org2.birthday = new Date();
		org2.parent = parent;
		
		parent.children.add(org2);
		
		Session session = sessionFactory.getCurrentSession();
		session.save(parent);
		
		return "";
	}
}

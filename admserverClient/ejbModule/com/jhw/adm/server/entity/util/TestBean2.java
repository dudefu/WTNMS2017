package com.jhw.adm.server.entity.util;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@SuperContent(clazz=TestBean1.class)
public class TestBean2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String ddds;
	private TestBean1 bean1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDdds() {
		return ddds;
	}

	public void setDdds(String ddds) {
		this.ddds = ddds;
	}


	@ManyToOne
	public TestBean1 getBean1() {
		return bean1;
	}

	public void setBean1(TestBean1 bean1) {
		this.bean1 = bean1;
	}

}

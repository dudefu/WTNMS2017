package com.jhw.adm.server.entity.util;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TestBean3{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String xxx;
	private TestBean1 bean1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public TestBean1 getBean1() {
		return bean1;
	}

	public void setBean1(TestBean1 bean1) {
		this.bean1 = bean1;
	}

	public String getXxx() {
		return xxx;
	}

	public void setXxx(String xxx) {
		this.xxx = xxx;
	}

}

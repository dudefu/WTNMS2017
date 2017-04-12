package com.jhw.adm.server.entity.util;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.IndexColumn;

@Entity
public class TestBean1 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Set<TestBean2> beans2;
	private Set<TestBean3> beans3;
	private String xxxx;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy="bean1",fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<TestBean2> getBeans2() {
		return beans2;
	}

	public void setBeans2(Set<TestBean2> beans2) {
		this.beans2 = beans2;
	}

	@OneToMany(mappedBy="bean1",fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Set<TestBean3> getBeans3() {
		return beans3;
	}

	public void setBeans3(Set<TestBean3> beans3) {
		this.beans3 = beans3;
	}

	public String getXxxx() {
		return xxxx;
	}



	public void setXxxx(String xxxx) {
		this.xxxx = xxxx;
	}

}

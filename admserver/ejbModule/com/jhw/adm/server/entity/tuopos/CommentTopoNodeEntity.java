package com.jhw.adm.server.entity.tuopos;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "topocommentnode")
@DiscriminatorValue(value = "CM")
public class CommentTopoNodeEntity extends NodeEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String guid;
	private String content;  //����
	private String comment;  //ע��
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	/**
	 * ����
	 * @return
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * ע��
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}

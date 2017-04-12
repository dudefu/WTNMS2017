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
	private String content;  //ÄÚÈÝ
	private String comment;  //×¢ÊÍ
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	/**
	 * ÄÚÈÝ
	 * @return
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * ×¢ÊÍ
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}

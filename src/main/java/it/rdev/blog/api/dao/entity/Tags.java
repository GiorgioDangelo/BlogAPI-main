package it.rdev.blog.api.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tags {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "tag", length = 50)
	private String tag;
	@ManyToOne
	@JoinColumn(name="id_articolo",referencedColumnName="id")
	private Articolo struttura_articolo;

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Articolo getStruttura_articolo() {
		return struttura_articolo;
	}
	public void setStruttura_articolo(Articolo struttura_articolo) {
		this.struttura_articolo = struttura_articolo;
	}

}

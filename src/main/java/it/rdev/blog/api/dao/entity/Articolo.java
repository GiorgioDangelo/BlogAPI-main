package it.rdev.blog.api.dao.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "articolo")
public class Articolo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column (name = "testo", length = 200,nullable = false)
	private String testo;
	@Column (name = "categoria", length = 50,nullable = false)
	private String categoria;
	@Column(name = "titolo", length = 50,nullable = false)
	private String titolo;
	@Column (name = "sottotitolo", length = 50)
	private String sottotitolo;
	@Column
	private Integer stato;
	@Column (name="data_di_pubblicazione",nullable = false)
	private Date data_di_pubblicazione;
	@Column
	private Date data_ultima_modifica;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public User getStruttura_user() {
		return struttura_user;
	}
	public void setStruttura_user(User struttura_user) {
		this.struttura_user = struttura_user;
	}
	@ManyToOne
	@JoinColumn(name="id_users",referencedColumnName="username")
	private User struttura_user;
	

	

	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getSottotitolo() {
		return sottotitolo;
	}
	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	public Integer getStato() {
		return stato;
	}
	public void setStato(Integer stato) {
		this.stato = stato;
	}
	public Date getData_di_pubblicazione() {
		return data_di_pubblicazione;
	}
	public void setData_di_pubblicazione(Date data_di_pubblicazione) {
		this.data_di_pubblicazione = data_di_pubblicazione;
	}
	public Date getData_ultima_modifica() {
		return data_ultima_modifica;
	}
	public void setData_ultima_modifica(Date data_ultima_modifica) {
		this.data_ultima_modifica = data_ultima_modifica;
	}

	
	
}

package it.rdev.blog.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.Tags;
@Repository
public interface TagsDao extends CrudRepository<Tags, Integer>{
	@Query("Select a.struttura_articolo from Tags a WHERE a.tag= :valore")
	List<Articolo> trovaTag(@Param("valore")String tag);

}

package it.rdev.blog.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.rdev.blog.api.controller.dto.TagsDTO;
import it.rdev.blog.api.dao.TagsDao;
import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.Tags;
@Service
public class TagUserDetailsService {
	@Autowired 
	private TagsDao tagsDao;

	//Qui valorizzo i dati dell'entity prendendo i dati dal DTO
	public Tags save(TagsDTO tag_dto,Articolo articolo_corrente) {
		Tags newTag = new Tags();
		newTag.setTag(tag_dto.getTag());
		newTag.setStruttura_articolo(articolo_corrente);
		return tagsDao.save(newTag);
	}
	// Mi restituisce i nomi dei tag,poichè l'ipotesi è che ci possano essere dei tag 0 ossia
	//quindi tag null ,prendo solo quelli valorizzati
	public List<String> getAllTags() {
		List<Tags> tutto=(List<Tags>) tagsDao.findAll();
		List<String> nomi_tags=new ArrayList<String>();
		if(tutto!=null) {
			for(int i=0;i<tutto.size();i++) {
				if(tutto.get(i).getTag()!=null) {
				nomi_tags.add(tutto.get(i).getTag());
			}}
		}
				
		return nomi_tags;
	}
}

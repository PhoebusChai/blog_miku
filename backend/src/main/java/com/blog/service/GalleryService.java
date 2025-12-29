package com.blog.service;

import com.blog.entity.Gallery;
import com.blog.mapper.GalleryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryService {

    @Autowired
    private GalleryMapper galleryMapper;

    public Gallery create(Gallery gallery) {
        if (gallery.getStatus() == null) {
            gallery.setStatus(1);
        }
        if (gallery.getSortOrder() == null) {
            gallery.setSortOrder(0);
        }
        galleryMapper.insert(gallery);
        return gallery;
    }

    public Gallery getById(Integer id) {
        return galleryMapper.selectById(id);
    }

    public List<Gallery> getAll() {
        return galleryMapper.selectAll();
    }

    public List<Gallery> getByCategory(String category) {
        return galleryMapper.selectByCategory(category);
    }

    public List<Gallery> getVisible() {
        return galleryMapper.selectVisible();
    }

    public void update(Gallery gallery) {
        galleryMapper.updateById(gallery);
    }

    public void delete(Integer id) {
        galleryMapper.deleteById(id);
    }

    public List<String> getAllCategories() {
        return galleryMapper.selectAllCategories();
    }

    public Long count() {
        return galleryMapper.count();
    }
}

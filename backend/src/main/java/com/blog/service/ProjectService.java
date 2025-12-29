package com.blog.service;

import com.blog.entity.Project;
import com.blog.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    public Project create(Project project) {
        if (project.getStatus() == null) {
            project.setStatus(0);
        }
        if (project.getSortOrder() == null) {
            project.setSortOrder(0);
        }
        if (project.getViewCount() == null) {
            project.setViewCount(0);
        }
        projectMapper.insert(project);
        return project;
    }

    public Project getById(Integer id) {
        return projectMapper.selectById(id);
    }

    public List<Project> getAll() {
        return projectMapper.selectAll();
    }

    public List<Project> getByStatus(Integer status) {
        return projectMapper.selectByStatus(status);
    }

    public void update(Project project) {
        projectMapper.updateById(project);
    }

    public void delete(Integer id) {
        projectMapper.deleteById(id);
    }

    public void incrementViewCount(Integer id) {
        projectMapper.incrementViewCount(id);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", projectMapper.count());
        stats.put("active", projectMapper.countByStatus(0));
        stats.put("completed", projectMapper.countByStatus(1));
        stats.put("archived", projectMapper.countByStatus(2));
        return stats;
    }
}

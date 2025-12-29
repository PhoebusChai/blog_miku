package com.blog.service;

import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendLinkService {

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    public FriendLink create(FriendLink friendLink) {
        if (friendLink.getStatus() == null) {
            friendLink.setStatus(0);
        }
        if (friendLink.getSortOrder() == null) {
            friendLink.setSortOrder(0);
        }
        friendLinkMapper.insert(friendLink);
        return friendLink;
    }

    public FriendLink getById(Integer id) {
        return friendLinkMapper.selectById(id);
    }

    public List<FriendLink> getAll() {
        return friendLinkMapper.selectAll();
    }

    public List<FriendLink> getByStatus(Integer status) {
        return friendLinkMapper.selectByStatus(status);
    }

    public List<FriendLink> getApproved() {
        return friendLinkMapper.selectApproved();
    }

    public void update(FriendLink friendLink) {
        friendLinkMapper.updateById(friendLink);
    }

    public void updateStatus(Integer id, Integer status) {
        friendLinkMapper.updateStatus(id, status);
    }

    public void delete(Integer id) {
        friendLinkMapper.deleteById(id);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", friendLinkMapper.count());
        stats.put("pending", friendLinkMapper.countByStatus(0));
        stats.put("approved", friendLinkMapper.countByStatus(1));
        stats.put("rejected", friendLinkMapper.countByStatus(2));
        return stats;
    }
}

-- ============================================
-- 更新阅读记录表，添加点赞和收藏字段
-- ============================================

USE personal_blog;

-- 添加点赞和收藏字段
ALTER TABLE reading_history 
ADD COLUMN is_liked TINYINT DEFAULT 0 COMMENT '是否点赞：0-否，1-是' AFTER updated_at,
ADD COLUMN is_bookmarked TINYINT DEFAULT 0 COMMENT '是否收藏：0-否，1-是' AFTER is_liked;

-- 添加索引以提高查询性能
ALTER TABLE reading_history 
ADD INDEX idx_liked (is_liked),
ADD INDEX idx_bookmarked (is_bookmarked);

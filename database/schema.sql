-- ============================================
-- 个人博客数据库设计
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS personal_blog 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE personal_blog;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    name VARCHAR(50) NOT NULL COMMENT '用户名',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    role TINYINT UNSIGNED DEFAULT 0 COMMENT '用户角色：0-普通用户，1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 文章表
-- ============================================
CREATE TABLE IF NOT EXISTS articles (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容（Markdown格式）',
    cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    author_id INT UNSIGNED NOT NULL COMMENT '作者ID',
    view_count INT UNSIGNED DEFAULT 0 COMMENT '浏览次数',
    like_count INT UNSIGNED DEFAULT 0 COMMENT '点赞次数',
    comment_count INT UNSIGNED DEFAULT 0 COMMENT '评论次数',
    status TINYINT UNSIGNED DEFAULT 0 COMMENT '状态：0-草稿，1-已发布，2-仅自己可见，3-已删除',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：1-是，0-否',
    published_at TIMESTAMP NULL DEFAULT NULL COMMENT '发布时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_author (author_id),
    INDEX idx_status (status),
    INDEX idx_published_at (published_at),
    INDEX idx_is_top (is_top),
    FULLTEXT idx_title_content (title, content) COMMENT '全文搜索索引',
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================
-- 标签表
-- ============================================
CREATE TABLE IF NOT EXISTS tags (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    slug VARCHAR(50) NOT NULL UNIQUE COMMENT '标签别名（URL友好）',
    article_count INT UNSIGNED DEFAULT 0 COMMENT '文章数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================
-- 文章标签关联表
-- ============================================
CREATE TABLE IF NOT EXISTS article_tags (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    article_id INT UNSIGNED NOT NULL COMMENT '文章ID',
    tag_id INT UNSIGNED NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    INDEX idx_article (article_id),
    INDEX idx_tag (tag_id),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================
-- 评论表（留言板）
-- ============================================
CREATE TABLE IF NOT EXISTS comments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    article_id INT UNSIGNED DEFAULT NULL COMMENT '文章ID（NULL表示留言板评论）',
    user_id INT UNSIGNED DEFAULT NULL COMMENT '用户ID（NULL表示游客）',
    parent_id INT UNSIGNED DEFAULT NULL COMMENT '父评论ID（用于回复）',
    guest_name VARCHAR(50) DEFAULT NULL COMMENT '游客昵称',
    guest_email VARCHAR(100) DEFAULT NULL COMMENT '游客邮箱',
    content TEXT NOT NULL COMMENT '评论内容',
    ip_address VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
    like_count INT UNSIGNED DEFAULT 0 COMMENT '点赞次数',
    status TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-垃圾评论',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_article (article_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 项目表
-- ============================================
CREATE TABLE IF NOT EXISTS projects (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
    title VARCHAR(100) NOT NULL COMMENT '项目名称',
    description TEXT DEFAULT NULL COMMENT '项目描述',
    cover_image VARCHAR(500) DEFAULT NULL COMMENT '项目封面',
    demo_url VARCHAR(200) DEFAULT NULL COMMENT '演示地址',
    github_url VARCHAR(200) DEFAULT NULL COMMENT 'GitHub地址',
    tech_stack JSON DEFAULT NULL COMMENT '技术栈（JSON数组）',
    status TINYINT UNSIGNED DEFAULT 0 COMMENT '项目状态：0-进行中，1-已完成，2-已归档',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    view_count INT UNSIGNED DEFAULT 0 COMMENT '浏览次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- ============================================
-- 图库表
-- ============================================
CREATE TABLE IF NOT EXISTS gallery (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '图片ID',
    title VARCHAR(100) DEFAULT NULL COMMENT '图片标题',
    description TEXT DEFAULT NULL COMMENT '图片描述',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    thumbnail_url VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT UNSIGNED DEFAULT 1 COMMENT '状态：0-隐藏，1-显示，2-删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图库表';

-- ============================================
-- 友情链接表
-- ============================================
CREATE TABLE IF NOT EXISTS friend_links (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '链接ID',
    name VARCHAR(50) NOT NULL COMMENT '网站名称',
    url VARCHAR(200) NOT NULL COMMENT '网站地址',
    avatar VARCHAR(500) DEFAULT NULL COMMENT '网站头像',
    description VARCHAR(200) DEFAULT NULL COMMENT '网站描述',
    status TINYINT UNSIGNED DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- ============================================
-- 网站配置表
-- ============================================
CREATE TABLE IF NOT EXISTS site_config (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT DEFAULT NULL COMMENT '配置值',
    config_type TINYINT UNSIGNED DEFAULT 0 COMMENT '配置类型：0-字符串，1-数字，2-布尔值，3-JSON',
    description VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网站配置表';

-- ============================================
-- 阅读记录表
-- ============================================
CREATE TABLE IF NOT EXISTS reading_history (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID',
    article_id INT UNSIGNED NOT NULL COMMENT '文章ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '首次阅读时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后阅读时间',
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_user (user_id),
    INDEX idx_article (article_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读记录表';

-- ============================================
-- 访问统计表
-- ============================================
CREATE TABLE IF NOT EXISTS visit_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id INT UNSIGNED DEFAULT NULL COMMENT '用户ID',
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    referer VARCHAR(500) DEFAULT NULL COMMENT '来源页面',
    path VARCHAR(500) NOT NULL COMMENT '访问路径',
    method VARCHAR(10) DEFAULT 'GET' COMMENT '请求方法',
    status_code INT DEFAULT NULL COMMENT '响应状态码',
    response_time INT DEFAULT NULL COMMENT '响应时间（毫秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    INDEX idx_user (user_id),
    INDEX idx_ip (ip_address),
    INDEX idx_path (path(100)),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问统计表';

-- ============================================
-- 插入初始数据
-- ============================================

-- 插入管理员用户（密码：123456，需要在应用层加密）
INSERT INTO users (email, password, name, avatar, role) VALUES
('admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 1),
('user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '普通用户', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user', 0);

-- 插入标签
INSERT INTO tags (name, slug) VALUES
('Vue.js', 'vuejs'),
('TypeScript', 'typescript'),
('JavaScript', 'javascript'),
('CSS', 'css'),
('Node.js', 'nodejs'),
('React', 'react'),
('前端开发', 'frontend'),
('后端开发', 'backend'),
('数据库', 'database'),
('算法', 'algorithm');

-- 插入示例文章
INSERT INTO articles (title, summary, content, author_id, status, published_at) VALUES
('欢迎来到我的博客', '这是我的第一篇博客文章，记录我的技术成长之路。', '# 欢迎来到我的博客\n\n这是我的第一篇博客文章。在这里，我会分享我的技术学习心得、项目经验和生活感悟。\n\n## 关于我\n\n我是一名热爱技术的开发者，专注于前端开发。\n\n## 博客内容\n\n- 技术文章\n- 项目分享\n- 学习笔记\n- 生活随笔', 1, 1, NOW()),
('Vue 3 Composition API 实践', '深入探讨 Vue 3 Composition API 的使用技巧和最佳实践。', '# Vue 3 Composition API 实践\n\n## 简介\n\nVue 3 带来了全新的 Composition API，让我们能够更好地组织和复用代码逻辑。\n\n## 核心概念\n\n### setup 函数\n\n```javascript\nimport { ref, computed } from ''vue''\n\nexport default {\n  setup() {\n    const count = ref(0)\n    const double = computed(() => count.value * 2)\n    \n    return { count, double }\n  }\n}\n```\n\n## 总结\n\nComposition API 为我们提供了更灵活的代码组织方式。', 1, 1, NOW());

-- 插入文章标签关联
INSERT INTO article_tags (article_id, tag_id) VALUES
(1, 7),
(2, 1),
(2, 2),
(2, 7);

-- 插入网站配置
INSERT INTO site_config (config_key, config_value, config_type, description) VALUES
-- 基本设置
('site_name', '落叶无痕的博客', 0, '网站名称'),
('site_description', '分享技术、记录生活、探索世界', 0, '网站描述'),
('site_keywords', 'Vue,TypeScript,前端开发,博客,技术分享', 0, '网站关键词'),
('site_logo', '/logo.png', 0, '网站Logo'),
('site_favicon', '/favicon.ico', 0, '网站图标'),
('site_start_date', '2024-01-01', 0, '网站创建日期'),
('site_copyright', '© 2024 落叶无痕. All rights reserved.', 0, '版权信息'),
-- 博主信息
('blogger_avatar', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix', 0, '博主头像'),
('blogger_name', '落叶无痕', 0, '博主昵称'),
('blogger_role', '全栈开发工程师', 0, '博主职位'),
('blogger_location', '中国 · 北京', 0, '博主所在地'),
('blogger_bio', '热爱编程，喜欢探索新技术。专注于前端开发，对用户体验和界面设计有着浓厚的兴趣。相信技术能够改变世界，代码可以创造美好。', 0, '博主简介'),
('blogger_skills', 'Vue.js,TypeScript,Node.js,UI/UX', 0, '博主技能'),
('blogger_interests', '编程,摄影,音乐,阅读,游戏,旅行', 0, '博主兴趣'),
-- 社交媒体
('social_github', 'https://github.com/username', 0, 'GitHub地址'),
('social_bilibili', 'https://space.bilibili.com/uid', 0, '哔哩哔哩地址'),
('social_email', 'contact@example.com', 0, '联系邮箱'),
('social_twitter', '', 0, 'Twitter地址'),
('social_weibo', '', 0, '微博地址'),
('social_zhihu', '', 0, '知乎地址'),
('social_wechat', '', 0, '微信公众号');

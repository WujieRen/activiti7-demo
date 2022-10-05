SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- 创建用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `name` varchar(32) DEFAULT NULL COMMENT '姓名',
                        `address` varchar(64) DEFAULT NULL COMMENT '联系地址',
                        `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '账号',
                        `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
                        `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- 填充用户表 密码：123
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admincn', 'beijing', 'admin', '$2a$10$8ZuuTOgr.wYM7kcbgdIT1eeYD9ZUTd3qJFM6CKvwieDlrQO5K7qIC', 'ROLE_ACTIVITI_ADMIN');
INSERT INTO `user` VALUES ('2', 'ygcn', 'shanghang', 'yuangong', '$2a$10$8ZuuTOgr.wYM7kcbgdIT1eeYD9ZUTd3qJFM6CKvwieDlrQO5K7qIC', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam,g_bajiewukong');
INSERT INTO `user` VALUES ('3', 'zgcn', 'beijing', 'zhuguan', '$2a$10$8ZuuTOgr.wYM7kcbgdIT1eeYD9ZUTd3qJFM6CKvwieDlrQO5K7qIC', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam');
INSERT INTO `user` VALUES ('4', 'ldcn', 'beijing', 'lingdao', '$2a$10$8ZuuTOgr.wYM7kcbgdIT1eeYD9ZUTd3qJFM6CKvwieDlrQO5K7qIC', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam');

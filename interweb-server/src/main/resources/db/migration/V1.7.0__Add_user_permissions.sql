CREATE TABLE IF NOT EXISTS `user_permission`
(
    `user_id`    BIGINT UNSIGNED,
    `permission` VARCHAR(50),
    `created`    DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, permission),
    CONSTRAINT `user_permission_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

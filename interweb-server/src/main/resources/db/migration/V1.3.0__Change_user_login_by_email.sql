ALTER TABLE `user`
    DROP COLUMN `username`;
ALTER TABLE `user`
    DROP COLUMN `password`;

ALTER TABLE `user`
    ADD COLUMN `approved` BOOL DEFAULT 0 AFTER `role`;
ALTER TABLE `user`
    ADD `updated` DATETIME DEFAULT NOW() AFTER `approved`;
ALTER TABLE `user`
    ADD `created` DATETIME DEFAULT NOW() AFTER `updated`;
ALTER TABLE `user`
    CHANGE COLUMN `role` `role` ENUM ('User', 'Admin') NOT NULL DEFAULT 'User' AFTER `email`;

CREATE TABLE IF NOT EXISTS `user_token`
(
    `id`      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `type`    VARCHAR(64)     NOT NULL,
    `token`   VARCHAR(255)    NOT NULL,
    `created` DATETIME DEFAULT NOW(),
    UNIQUE KEY `index_user_token_token` (`token`),
    CONSTRAINT `fk_user_token_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

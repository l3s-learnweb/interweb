CREATE TABLE IF NOT EXISTS `chat`
(
    `id`             UUID             NOT NULL PRIMARY KEY,
    `token_id`       BIGINT UNSIGNED  NOT NULL,
    `model`          VARCHAR(32)      NOT NULL,
    `user`           VARCHAR(32)               DEFAULT NULL,
    `title`          VARCHAR(512)              DEFAULT NULL,
    `used_tokens`    int(11) UNSIGNED NOT NULL DEFAULT 0,
    `estimated_cost` DOUBLE UNSIGNED  NOT NULL DEFAULT 0,
    `created`        DATETIME                  DEFAULT NOW(),
    KEY `index_chat_user` (`user`),
    CONSTRAINT `fk_chat_token` FOREIGN KEY (`token_id`) REFERENCES `user_token` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `chat_message`
(
    `id`      BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chat_id` UUID             NOT NULL,
    `role`    TINYINT UNSIGNED NOT NULL,
    `content` TEXT             NOT NULL,
    `created` DATETIME DEFAULT NOW(),
    CONSTRAINT `fk_chat_message_chat` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `user`
(
    `id`       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `email`    VARCHAR(255)    NOT NULL,
    `password` VARCHAR(255)    NOT NULL,
    `username` VARCHAR(255)    NOT NULL,
    `role`     VARCHAR(255)    NOT NULL
);

CREATE TABLE IF NOT EXISTS `user_token`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT UNSIGNED NOT NULL,
    `name`        VARCHAR(255)    NOT NULL,
    `url`         VARCHAR(512)  DEFAULT NULL,
    `description` VARCHAR(1024) DEFAULT NULL,
    `apikey`      VARCHAR(64)     NOT NULL,
    UNIQUE KEY `index_user_token_apikey` (`apikey`),
    CONSTRAINT `fk_user_token_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

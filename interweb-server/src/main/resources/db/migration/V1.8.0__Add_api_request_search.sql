CREATE TABLE IF NOT EXISTS `api_request_search`
(
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT UNSIGNED  NOT NULL,
    `apikey_id`     BIGINT UNSIGNED  NULL,
    `engine`        VARCHAR(32)      NOT NULL,
    `content_type`  VARCHAR(32)      NOT NULL,
    `query`         VARCHAR(512)      NOT NULL,
    `est_cost`      DOUBLE UNSIGNED  NOT NULL DEFAULT 0,
    `created`       DATETIME         NOT NULL DEFAULT NOW(),
    CONSTRAINT `api_request_search_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `api_request_search_apikey` FOREIGN KEY (`apikey_id`) REFERENCES `api_key` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

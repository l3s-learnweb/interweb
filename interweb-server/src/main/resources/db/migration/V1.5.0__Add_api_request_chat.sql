CREATE TABLE IF NOT EXISTS `api_request_chat`
(
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`       BIGINT UNSIGNED  NOT NULL,
    `apikey_id`     BIGINT UNSIGNED  NULL,
    `model`         VARCHAR(32)      NOT NULL,
    `input_tokens`  int(11) UNSIGNED NOT NULL DEFAULT 0,
    `output_tokens` int(11) UNSIGNED NOT NULL DEFAULT 0,
    `est_cost`      DOUBLE UNSIGNED  NOT NULL DEFAULT 0,
    `created`       DATETIME         NOT NULL DEFAULT NOW(),
    CONSTRAINT `api_request_chat_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `api_request_chat_apikey` FOREIGN KEY (`apikey_id`) REFERENCES `user_apikey` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

ALTER TABLE `user_apikey` RENAME TO `api_key`;

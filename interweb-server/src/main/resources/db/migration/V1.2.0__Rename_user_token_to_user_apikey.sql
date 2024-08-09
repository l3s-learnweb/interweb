ALTER TABLE `user_token`
    DROP CONSTRAINT `index_user_token_apikey`;
ALTER TABLE `user_token`
    DROP CONSTRAINT `fk_user_token_user`;

ALTER TABLE `user_token` RENAME TO `user_apikey`;
ALTER TABLE `user_apikey`
    ADD `created` DATETIME DEFAULT NOW() AFTER `apikey`;
ALTER TABLE `user_apikey`
    ADD UNIQUE KEY `index_user_apikey_apikey` (`apikey`);
ALTER TABLE `user_apikey`
    ADD CONSTRAINT `fk_user_user_apikey` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `chat`
    DROP CONSTRAINT `fk_chat_token`;
ALTER TABLE `chat` RENAME COLUMN `token_id` TO `apikey_id`;
ALTER TABLE `chat`
    ADD CONSTRAINT `fk_chat_apikey` FOREIGN KEY (`apikey_id`) REFERENCES `user_apikey` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

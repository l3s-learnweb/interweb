ALTER TABLE `user_token` RENAME TO `user_apikey`;

ALTER TABLE `user_apikey` ADD `created` DATETIME DEFAULT NOW() AFTER `apikey`;
ALTER TABLE `chat` RENAME COLUMN `token_id` TO `apikey_id`;

ALTER TABLE `chat`
    CHANGE COLUMN `model` `model` VARCHAR(64) NOT NULL;

ALTER TABLE `api_request_chat`
    CHANGE COLUMN `model` `model` VARCHAR(64) NOT NULL;

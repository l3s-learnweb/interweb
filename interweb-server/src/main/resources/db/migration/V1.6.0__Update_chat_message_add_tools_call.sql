ALTER TABLE `chat_message`
    CHANGE COLUMN `content` `content` TEXT DEFAULT NULL;
ALTER TABLE `chat_message`
    ADD COLUMN `tool_calls` TEXT DEFAULT NULL AFTER `content`;

ALTER TABLE `chat`
    ADD `updated` DATETIME DEFAULT NOW() AFTER `estimated_cost`;

ALTER TABLE `chat`
    RENAME COLUMN `estimated_cost` TO `est_cost`;

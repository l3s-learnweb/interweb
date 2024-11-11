ALTER TABLE `user`
    ADD COLUMN `monthly_budget` DOUBLE DEFAULT 1 AFTER `approved`;

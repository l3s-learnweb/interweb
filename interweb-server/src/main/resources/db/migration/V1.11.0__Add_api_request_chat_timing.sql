ALTER TABLE `api_request_chat`
    ADD COLUMN `elapsed_time`        BIGINT UNSIGNED NULL AFTER `est_cost`,
    ADD COLUMN `total_time`          BIGINT UNSIGNED NULL AFTER `elapsed_time`,
    ADD COLUMN `load_time`           BIGINT UNSIGNED NULL AFTER `total_time`,
    ADD COLUMN `prompt_eval_time`    BIGINT UNSIGNED NULL AFTER `load_time`,
    ADD COLUMN `completion_gen_time` BIGINT UNSIGNED NULL AFTER `prompt_eval_time`;

INSERT INTO flag (flag_id, flag_description, rule_id, message_id, created_at)
VALUES ('flag1', 'Potential compliance violation', 'rule001', 'msg1', UNIX_TIMESTAMP() * 1000);

INSERT INTO flag (flag_id, flag_description, rule_id, message_id, created_at)
VALUES ('flag2', 'Sensitive information detected', 'rule002', 'msg2', UNIX_TIMESTAMP() * 1000);``
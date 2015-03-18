DELETE FROM security_question_response;
DELETE FROM security_question;

INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-horse', 'What was your high school mascot?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-baby', 'What is the middle name of your first child?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-heart', 'Where did you go on your honeymoon?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-car', 'What was your first car?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-paw', 'What was the name of your first pet?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-sign', 'What was the street you grew up on?');
INSERT INTO security_question (enabled, iconPath, question) VALUES (1, 'ic-woman', 'What is your mother\'s maiden name?');

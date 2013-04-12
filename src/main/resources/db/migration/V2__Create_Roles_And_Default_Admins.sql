/**
 * Creates default user authorities
 **/
replace into authority (authority) values ('ROLE_USER');
replace into authority (authority) values ('ROLE_ADMIN');
insert into user_authority (user_id, authority_id) (SELECT user.id, (SELECT id from authority where authority = 'ROLE_USER') from user);
/**
 * Updates default user authorities
 **/
update authority set authority = 'ROLE_CAS_USER' WHERE authority = 'ROLE_USER';
update authority set authority = 'ROLE_CAS_ADMIN' WHERE authority = 'ROLE_ADMIN';
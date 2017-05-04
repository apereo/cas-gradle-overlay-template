package org.apereo.cas.infusionsoft.exceptions;

import org.apereo.cas.infusionsoft.domain.UserAccount;

import java.util.List;

/**
 * Indicates that there are conflicting user account records
 */
public class DuplicateAccountException extends AccountException {
    private final List<UserAccount> duplicateAccounts;

    public DuplicateAccountException(List<UserAccount> duplicateAccounts) {
        super("Duplicate accounts found", null);
        this.duplicateAccounts = duplicateAccounts;
    }

    public List<UserAccount> getDuplicateAccounts() {
        return duplicateAccounts;
    }
}

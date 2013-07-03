package com.infusionsoft.cas.exceptions;

import com.infusionsoft.cas.domain.UserAccount;

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

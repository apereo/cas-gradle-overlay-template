package com.infusionsoft.cas.exceptions;

import com.infusionsoft.cas.domain.UserAccount;

import java.util.List;

/**
 * Indicates something went wrong with the mapping of CAS accounts to app user accounts.
 */
public class AccountException extends Exception {
    private final List<UserAccount> duplicateAccounts;

    public AccountException(String message, Throwable cause) {
        super(message, cause);
        this.duplicateAccounts = null;
    }

    public AccountException(List<UserAccount> duplicateAccounts) {
        super("Duplicate accounts found");
        this.duplicateAccounts = duplicateAccounts;
    }

    public List<UserAccount> getDuplicateAccounts() {
        return duplicateAccounts;
    }
}

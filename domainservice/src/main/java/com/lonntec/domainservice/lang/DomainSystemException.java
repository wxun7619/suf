package com.lonntec.domainservice.lang;

import team.benchem.framework.lang.MicroServiceException;
import team.benchem.framework.lang.StateCode;

public class DomainSystemException extends MicroServiceException {

    public DomainSystemException(StateCode stateCode) {
        super(stateCode);
    }

    public DomainSystemException(StateCode stateCode, String message) {
        super(stateCode, message);
    }

    public DomainSystemException(StateCode stateCode, Throwable cause) {
        super(stateCode, cause);
    }

    public DomainSystemException(StateCode stateCode, String message, Throwable cause) {
        super(stateCode, message, cause);
    }
}

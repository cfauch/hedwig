/*
 * Copyright 2019 Claire Fauch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.code.fauch.hedwig;

/**
 * All available TFTP error codes.
 * 
 * @author c.fauch
 *
 */
public enum EError {
    
    NOT_DEFINED((short) 0),
    FILE_NOT_FOUND((short) 1),
    ACCESS_VIOLATION((short) 2),
    DISK_FULL_OR_ALLOCATION_EXCEED((short) 3),
    ILLEGAL_TFTP_OPERATION((short) 4),
    UNKNOWN_TRANSFER_ID((short) 5),
    FILE_ALREADY_EXISTS((short) 6),
    NO_SUCH_USER((short) 7),
    OPTION_NEGOTIATION_FAILED((short) 8);

    /**
     * error code
     */
    private final short code;

    /**
     * No constructor.
     * 
     * @param code error code
     */
    private EError(final short code) {
        this.code = code;
    }

    /**
     * Returns the error code.
     * @return error code
     */
    public short getCode() {
        return this.code;
    }

    /**
     * Returns the error corresponding to given code.
     * @param code the error code 
     * @return the corresponding error.
     */
    public static EError from(final short code) {
        for (EError op : EError.values()) {
            if (op.getCode() == code) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown EError code: " + code);
    }
    
}

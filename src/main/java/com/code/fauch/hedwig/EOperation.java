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
 * All available TFTP operation.
 * 
 * @author c.fauch
 *
 */
enum EOperation {
    READ((short) 1), 
    WRITE((short) 2), 
    DATA((short) 3), 
    ACK((short) 4), 
    ERROR((short) 5),
    OACK((short) 6);

    /**
     * Operation code.
     */
    private final short code;

    /**
     * No constructor.
     * 
     * @param code the operation code
     */
    private EOperation(final short code) {
        this.code = code;
    }

    /**
     * Returns the operation code.
     * 
     * @return code
     */
    short getCode() {
        return this.code;
    }

    /**
     * Returns the operation corresponding to given code.
     * 
     * @param code the operation code
     * @return the corresponding operation
     */
    static EOperation from(final short code) {
        for (EOperation op : EOperation.values()) {
            if (op.getCode() == code) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown EOperation code: " + code);
    }
    
}

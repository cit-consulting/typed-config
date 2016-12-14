package com.github.steveash.typedconfig.util;

public class HashCodeBuilder {
    private final int iConstant;
    private int iTotal = 0;

    public HashCodeBuilder(final int initialOddNumber, final int multiplierOddNumber) {
        if (initialOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
        }
        if (multiplierOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
        }
        iConstant = multiplierOddNumber;
        iTotal = initialOddNumber;
    }

    public Integer build() {
        return iTotal;
    }

    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            iTotal = iTotal * iConstant;

        } else {
            if (object.getClass().isArray()) {
                // factor out array case in order to keep method small enough
                // to be inlined
                appendArray(object);
            } else {
                iTotal = iTotal * iConstant + object.hashCode();
            }
        }
        return this;
    }

    private void appendArray(final Object object) {
        // 'Switch' on type of array, to dispatch to the correct handler
        // This handles multi dimensional arrays
        if (object instanceof long[]) {
            append(object);
        } else if (object instanceof int[]) {
            append(object);
        } else if (object instanceof short[]) {
            append(object);
        } else if (object instanceof char[]) {
            append(object);
        } else if (object instanceof byte[]) {
            append(object);
        } else if (object instanceof double[]) {
            append(object);
        } else if (object instanceof float[]) {
            append(object);
        } else if (object instanceof boolean[]) {
            append(object);
        } else {
            // Not an array of primitives
            append(object);
        }
    }
}

package com.lingona.cd4j.core;

/**
 *
 * Copied from Dot.Net framework by Esfand
 */

// Summary:
//     Defines a generalized method that a value type or class implements to create
//     a type-specific method for determining equality of instances.
//
// Type parameters:
//   T: The type of objects to compare.
public interface IEquatable<T> {
    
    // Summary: Indicates whether the current object is equal to another object of the same type.
    //
    // Parameters:
    //   other: An object to compare with this object.
    //
    // Returns: true if the current object is equal to the other parameter; otherwise, false.
    public boolean equalsTo(T other);
}

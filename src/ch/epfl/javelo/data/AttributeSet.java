package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * An AttributeSet
 *
 * @author Wesley Nana Davies(344592)
 * @author David Farah (341017)
 */
public record AttributeSet(long bits) {

    /**
     * @throws IllegalArgumentException
                if the AttributeSet is invalid (if some bits should not be equal to 1)
     */
    public AttributeSet{
        //check if any bit with index larger than the number of attributes is 1
        Preconditions.checkArgument(((Long.MAX_VALUE << Attribute.COUNT) & bits) == 0);
    }


    /**
     * Another constructor of the class which creates an AttributeSet
     with the collection of attributes given as parameters.
     * @param attributes collection of attributes to be added to the attributeSet
     * @return an AttributeSet
     */
    public static AttributeSet of(Attribute... attributes){
        long bits = 0;
        for (Attribute attribute : attributes){
            Preconditions.checkArgument(attribute.ordinal() < 64);
            bits |= (1L << attribute.ordinal());      // | is the bit-wise OR operator (|= equivalent to += for bits)
        }
        return new AttributeSet(bits);
    }


    /**
     * Returns true if the AttributeSet contains the attribute given as a parameter.
     * @param attribute to check in the set
     * @return a boolean value <code>true</code> if the Attribute set contains a specific attribute or
     * <code>false</code> if not
     */
    public boolean contains(Attribute attribute){
        return (bits & (1L << attribute.ordinal())) != 0;
    }


    /**
     * Returns true if the two AttributeSets intersect or false otherwise.
     * @param that other AttributeSet to be compared with <code>this</code>
     * @return <code>true</code> if the two Attribute sets have common elements and <code>false</code> otherwise
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }


    /**
     * Returns a String which is a textual representation of an AttributeSet.
     * @return a textual representation of an AttributeSet.
     */
    @Override
    public String toString(){
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attribute : Attribute.ALL){
            if (contains(attribute)){
                j.add(attribute.toString());
            }
        }
        return j.toString();
    }
}

// bitwise operators:
// - & (bitwise and): Binary AND Operator copies a bit to the result if it exists in both operands.
// - | (bitwise or): Binary OR Operator copies a bit if it exists in either operand.
// - ^ (bitwise XOR): Binary XOR Operator copies the bit if it is set in one operand but not both.
// - ~ (bitwise compliment): Binary Ones Complement Operator is unary and has the effect of 'flipping' bits.
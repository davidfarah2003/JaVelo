package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    /** Constructor
     * @param bits
     */
    public AttributeSet{
        //check if any bit with index larger than the number of attributes is 1
        Preconditions.checkArgument(((Long.MAX_VALUE << Attribute.COUNT) & bits) == 0);
    }


    /**
     * @param attributes attributes to add to attributeSet
     * @return a new AttributeSet with elements
     */
    public static AttributeSet of(Attribute... attributes){
        long bits = 0;
        for (Attribute attribute : attributes){
          //  Preconditions.checkArgument(attribute.ordinal() <= 64);
            bits |= (1L << attribute.ordinal());      // | is the bit-wise OR operator (|= equivalent to += for bits)
        }
        return new AttributeSet(bits);
    }


    /**
     * @param attribute
     * @return a boolean value <code>true</code> if the Attribute set contains a specific attribute or
     * <code>false</code> if not
     */
    public boolean contains(Attribute attribute){
        return (bits & (1L << attribute.ordinal())) != 0;
    }

    /**
     * @param that
     * @return <code>true</code> if the two Attribute sets have common elements and <code>false</code> otherwise
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

    /**
     * @return a textual representation of an Attribute Set.
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
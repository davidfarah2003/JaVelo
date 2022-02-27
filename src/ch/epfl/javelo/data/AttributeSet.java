package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    /**
     * @param bits
     */
    public AttributeSet{
        //I don't get this test
        long bitsTEST = 0b11L;
        bitsTEST = bitsTEST << 62;
        Preconditions.checkArgument((bitsTEST & bits) == 0);
    }

    //Seems OK
    public static AttributeSet of(Attribute... attributes){
        long bits = 0b0L;
        for (Attribute attribute : attributes){
            bits |= 1L << attribute.ordinal();      // | is the bit-wise OR operator (|= equivalent to += for bits)
        }
        return new AttributeSet(bits);
        // 62 attributs dans la classe Attribute.
    }


    /**
     * @param attribute
     * @return a boolean value <code>true</code> if the Attribute set contains
     * a specific attribute or <code>false</code> if not
     */
    public boolean contains(Attribute attribute){
        return (bits & (1L << attribute.ordinal())) != 0; //added parenthesis on 1L << attribute.ordinal() order matters
    }

    /**
     * @param that
     * @return a boolean value <code>true</code> if the two Attribute sets
     * have common elements or <code>false</code> if not
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    } //OK

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
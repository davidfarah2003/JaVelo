package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    /**
     *
     * @param bits
     */
    public AttributeSet{
        long bitsTEST = 0b11L;
        bitsTEST = bitsTEST << 62;
        Preconditions.checkArgument((bitsTEST & bits) == 0);
    }

    public static AttributeSet of(Attribute... attributes){
        long bits = 0b0L;
        for (Attribute attribute : attributes){
            bits |= 1L << attribute.ordinal();
        }
        return new AttributeSet(bits);
        // 62 attributs dans la classe Attribute.
    }

    /**
     *
     * @param attribute
     * @return a boolean value <code>true</code> if the Attribute set
     *  contains a specific attribute or <code>false</code> if not
     *
     *
     */
    public boolean contains(Attribute attribute){
        return (bits & 1L << attribute.ordinal()) != 0;
    }

    /**
     *
     * @param that
     * @return a boolean value <code>true</code> if the two Attribute sets
     * have common elements or <code>false</code> if not
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

    /**
     *
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

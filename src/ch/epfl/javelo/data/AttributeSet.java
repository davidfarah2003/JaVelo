package ch.epfl.javelo.data;

import java.util.StringJoiner;

public record AttributeSet(long bits) {
    /**
     *
     * @param bits
     */
    public AttributeSet{

    }

    public static AttributeSet of(Attribute... attributes){
        return new AttributeSet(6);
        // 62 attibuts dans la classe AttributeSet.
    }

    public boolean contains(Attribute attribute){
        return (bits & 1L << attribute.ordinal()) != 0;
    }

    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

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

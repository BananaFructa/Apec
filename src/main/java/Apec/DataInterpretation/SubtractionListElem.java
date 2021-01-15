package Apec.DataInterpretation;


public class SubtractionListElem {
    /** Name of the item */
    public String text;
    /** Quantity of that item */
    public int quant;
    /** The amount of ticks the element has left until is no longer shown on the screen */
    public int lifetme = 200;

    /**
     * @param s = Name of the item
     * @param q = Quantity of the item
     */
    public SubtractionListElem(String s,int q) {
        this.text = s;
        this.quant = q;
    }
}
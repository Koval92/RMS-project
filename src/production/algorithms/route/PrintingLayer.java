package production.algorithms.route;

public interface PrintingLayer {
    boolean get(int y, int x);
    void setFalse(int y, int x);
    void setTrue(int y, int x);
    int getWidth();
    int getHeight();
}

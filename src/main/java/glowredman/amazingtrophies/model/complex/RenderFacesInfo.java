package glowredman.amazingtrophies.model.complex;

public class RenderFacesInfo {

    private boolean yPos, yNeg;
    private boolean xPos, xNeg;
    private boolean zPos, zNeg;

    public RenderFacesInfo(final boolean state) {
        this.yPos = state;
        this.yNeg = state;
        this.xPos = state;
        this.xNeg = state;
        this.zPos = state;
        this.zNeg = state;
    }

    public boolean allHidden() {
        return (!yPos) && (!yNeg) && (!xPos) && (!xNeg) && (!zPos) && (!zNeg);
    }

    public boolean isYPos() {
        return yPos;
    }

    public boolean isYNeg() {
        return yNeg;
    }

    public boolean isXPos() {
        return xPos;
    }

    public boolean isXNeg() {
        return xNeg;
    }

    public boolean isZPos() {
        return zPos;
    }

    public boolean isZNeg() {
        return zNeg;
    }

    public void setYPos(boolean yPos) {
        this.yPos = yPos;
    }

    public void setYNeg(boolean yNeg) {
        this.yNeg = yNeg;
    }

    public void setXPos(boolean xPos) {
        this.xPos = xPos;
    }

    public void setXNeg(boolean xNeg) {
        this.xNeg = xNeg;
    }

    public void setZPos(boolean zPos) {
        this.zPos = zPos;
    }

    public void setZNeg(boolean zNeg) {
        this.zNeg = zNeg;
    }
}

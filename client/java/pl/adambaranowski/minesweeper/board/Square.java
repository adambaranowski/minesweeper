package pl.adambaranowski.minesweeper.board;

public class Square {
    private boolean hide;
    private String status;

    public Square(boolean hide, String status) {
        this.hide = hide;
        this.status = status;
    }

    public Square() {
        this.hide = true;
        this.status = " ";
    }

    public boolean isHide() {
        return hide;
    }

    public String getStatus() {
        return status;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package pl.adambaranowski.minesweeper.board;

import javafx.scene.control.ToggleButton;
import java.util.*;

public class Board {

    private Square[][] board;
    private int bombsCount;
    private int size;
    private HashMap<String, ToggleButton> buttons;


    private int squaresToUnhideNumber;
    private int unhiddenSquaresNumber;
    public double unhideBoardPercentage;



    public Board(int size, int bombsCount, HashMap<String, ToggleButton> buttons) {

        if (size > 7 ) {
            this.size = size;
            board = new Square[size][size];
        } else {
            this.size = 7;
            board = new Square[7][7];
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Square();
            }

        }

        if (bombsCount < board.length * board[0].length - 1 && bombsCount > 0) {
            this.bombsCount = bombsCount;
        } else {
            this.bombsCount = board.length * board[0].length - 1;
        }

        unhiddenSquaresNumber = 0;
        squaresToUnhideNumber = (int)Math.pow(size,2) - bombsCount;
        unhideBoardPercentage = 0.0;

        this.buttons = buttons;

        setBombs();
        setNumbers();
    }

    private void setBombs() {
        List<Integer> bombsPositions = new ArrayList<>();
        int scope = board.length * board[0].length;
        int bomb;

        Random random = new Random();

        while (bombsPositions.size() < bombsCount) {
            bomb = random.nextInt(scope);
            if (!bombsPositions.contains(bomb)) {
                bombsPositions.add(bomb);
            }

        }

        Collections.sort(bombsPositions);

        int x = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (bombsPositions.contains(x)) {
                    board[i][j].setStatus("o");
                }
                x++;
            }
        }

    }

    public void setNumbers() {

        int bombsNumber;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                bombsNumber = 0;

                if (board[i][j].getStatus().equals(" ")) {

                    for (Square s : neighbours(i, j).values()
                    ) {
                        if (s.getStatus().equals("o")) {
                            bombsNumber++;
                        }
                    }
                    if (bombsNumber > 0) {
                        board[i][j].setStatus(String.valueOf(bombsNumber));
                    }
                }
            }
        }
    }


    public Square boardIteratorByPosition(int position) {
        int counter = 1; //first position is 1 not 0
        try {
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[0].length; y++) {

                    if (counter == position) {
                        return board[x][y];
                    }
                    counter++;

                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    public HashMap<Integer, Square> neighbours(int row, int position) {
        HashMap<Integer, Square> neighbors = new HashMap<>();

        int positionInSeries = row * size + position + 1;
        //three squares above
        try {
            board[row][position - 1].getStatus(); //check if square not in the left frame

            if (boardIteratorByPosition(positionInSeries - board[0].length - 1) != null) {
                neighbors.put(0, boardIteratorByPosition(positionInSeries - board[0].length - 1));
            }


        } catch (Exception e) {
        }
        try {

            if (boardIteratorByPosition(positionInSeries - board[0].length) != null) {
                neighbors.put(1, boardIteratorByPosition(positionInSeries - board[0].length));
            }

        } catch (Exception e) {
        }
        try {
            board[row][position + 1].getStatus(); //check if the square not in the right frame

            if (boardIteratorByPosition(positionInSeries - board[0].length + 1) != null) {
                neighbors.put(2, boardIteratorByPosition(positionInSeries - board[0].length + 1));
            }
            ;

        } catch (Exception e) {
        }

        //two squares left and right
        try {
            board[row][position - 1].getStatus();

            if (boardIteratorByPosition(positionInSeries - 1) != null) {
                neighbors.put(3, boardIteratorByPosition(positionInSeries - 1));
            }
        } catch (Exception e) {
        }
        try {
            board[row][position + 1].getStatus();

            if (boardIteratorByPosition(positionInSeries + 1) != null) {
                neighbors.put(4, boardIteratorByPosition(positionInSeries + 1));
            }
        } catch (Exception e) {
        }

        //three squares at the bottom
        try {
            board[row][position - 1].getStatus();

            if (boardIteratorByPosition(positionInSeries + board[0].length - 1) != null) {
                neighbors.put(5, boardIteratorByPosition(positionInSeries + board[0].length - 1));
            }
        } catch (Exception e) {
        }
        try {

            if (boardIteratorByPosition(positionInSeries + board[0].length) != null) {
                neighbors.put(6, boardIteratorByPosition(positionInSeries + board[0].length));
            }

        } catch (Exception e) {
        }
        try {
            board[row][position + 1].getStatus();
            if (boardIteratorByPosition(positionInSeries + board[0].length + 1) != null) {
                neighbors.put(7, boardIteratorByPosition(positionInSeries + board[0].length + 1));
            }
        } catch (Exception e) {
        }

        return neighbors;
    }


    public void printBoard() {

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j].getStatus() + "  ");
            }
            System.out.print("             ");

            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].isHide()) {
                    System.out.print("H  ");
                } else {
                    System.out.print(board[i][j].getStatus() + "  ");
                }

            }

            System.out.println();
        }
    }

    //unhide fields in the model of board
    public void unhide(int row, int position, boolean ifCan) {
        try {
            boolean can = ifCan;
            int r = row;
            int p = position;

            if (!board[row][position].getStatus().equals(" ")|| !board[row][position].isHide()) {
                can= false;
            }

            if (!board[row][position].getStatus().equals("o")) { //&& board[row][position].isHide()) {
                board[row][position].setHide(false);

            }

            HashMap<Integer, Square> nb = neighbours(r, p);
            if (can) {

                //3 squares above
                if (p - 1 >= 0 && r - 1 >= 0) {//left
                    if (nb.get(0).getStatus() != null && nb.get(0).getStatus().equals(" ")) {
                        unhide(r - 1, p - 1, true);
                    } else {
                        unhide(r - 1, p - 1, false);
                    }
                }
                if (p + 1 <= board[0].length - 1 && r - 1 >= 0) {//right
                    if (nb.get(2).getStatus() != null && nb.get(2).getStatus().equals(" ")) {
                        unhide(r - 1, p + 1, true);
                    } else {
                        unhide(r - 1, p + 1, false);
                    }
                }
                if (r - 1 >= 0) {//centre
                    if (nb.get(1).getStatus() != null && nb.get(1).getStatus().equals(" ")) {
                        unhide(r - 1, p, true);
                    } else {
                        unhide(r - 1, p, false);
                    }
                }

                //3 squares down
                if (p - 1 >= 0 && r + 1 <= board.length) {
                    if (nb.get(5).getStatus() != null && nb.get(5).getStatus().equals(" ")) {
                        unhide(r + 1, p - 1, true);
                    } else {
                        unhide(r + 1, p - 1, false);
                    }
                }
                if (p + 1 <= board[0].length - 1 && r + 1 <= board.length) {
                    if (nb.get(7).getStatus() != null && nb.get(7).getStatus().equals(" ")) {
                        unhide(r + 1, p + 1, true);
                    } else {
                        unhide(r + 1, p + 1, false);
                    }
                }
                if (r + 1 <= board.length) {
                    if (nb.get(6).getStatus() != null && nb.get(6).getStatus().equals(" ")) {
                        unhide(r + 1, p, true);
                    } else {
                        unhide(r + 1, p, false);
                    }
                }
                //2 squares left and right

                if (p - 1 >= 0) {//2 zamiast 1

                    if (nb.get(3).getStatus() != null && nb.get(3).getStatus().equals(" ")) {
                        unhide(r, p - 1, true);
                    } else {
                        unhide(r, p - 1, false);
                    }
                }
                if (p + 1 <= board[0].length - 1) {
                    if (nb.get(4).getStatus() != null && nb.get(4).getStatus().equals(" ")) {
                        unhide(r, p + 1, true);
                    } else {
                        unhide(r, p + 1, false);
                    }
                }
            }
        } catch (NullPointerException e) {
        }
    }


    public void click(int row, int position) {
        unhide(row, position, true);
        unhideButtons();
        //unhideButtonsWhenWin();
    }

    //Unhide buttons while game is on
    private void unhideButtons(){
        String buttonId;
        unhiddenSquaresNumber=0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                if(!board[i][j].isHide()){
                    buttonId=String.valueOf(i)+ " " +String.valueOf(j);

                    buttons.get(buttonId).setText(board[i][j].getStatus());
                    buttons.get(buttonId).setSelected(true);
                    buttons.get(buttonId).setDisable(true);
                    unhiddenSquaresNumber++;



                }

            }
        }
        unhideBoardPercentage = (double)unhiddenSquaresNumber/squaresToUnhideNumber;

    }

    //Unhide all the board when player lost game
    public void unhideButtonsWhenLose(){
        String buttonId;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                buttonId=String.valueOf(i)+ " " + String.valueOf(j);
                buttons.get(buttonId).setText(board[i][j].getStatus());
                buttons.get(buttonId).setDisable(true);

            }
        }
    }

    //Add flag "P" to rest of buttons when player won
    public void unhideButtonsWhenWin(){
        String buttonId;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {


                buttonId=String.valueOf(i)+ " " + String.valueOf(j);
                buttons.get(buttonId).setText(board[i][j].getStatus());
                //System.out.println(buttonId);
                //System.out.println("i= "+ i + " j= "+j+" button id= "+buttonId);

                //100% of board is uncovered so buttons that are not selected are bombs
                //put "P" flag on them
                if(!buttons.get(buttonId).isSelected()){
                    buttons.get(buttonId).setText("P");
                }
            }
        }
    }

    public Square[][] getBoard() {
        return board;
    }

}

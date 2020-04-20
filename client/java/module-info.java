open module minesweeperGame {
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.json;

    exports pl.adambaranowski.minesweeper.main to javafx.graphics, javafx.controls, javafx.fxml;
}
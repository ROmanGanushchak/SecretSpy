module test_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens test_ui to javafx.fxml;
    exports test_ui;
}

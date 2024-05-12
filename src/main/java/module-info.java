module test_ui1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    opens test_ui to javafx.fxml;
    opens test_ui.Components to javafx.fxml;
    exports test_ui;
}

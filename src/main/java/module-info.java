module test_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    // requires org.aspectj.weaver;
    // requires org.aspectj.runtime;
    // requires org.aspectj.runtime;

    opens test_ui to javafx.fxml;
    opens test_ui.Components to javafx.fxml;
    exports test_ui;
}

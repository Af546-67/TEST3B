module com.aashish.aashishpizzaordering {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.aashish.aashishpizzaordering to javafx.fxml;
    exports com.aashish.aashishpizzaordering;
}
module org.pwio.elevatoranimation {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.pwio.elevatoranimation to javafx.fxml;
    exports org.pwio.elevatoranimation;
}
module org.example.ingatlankezelo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.ingatlankezelo to javafx.fxml;
    exports org.example.ingatlankezelo;
}
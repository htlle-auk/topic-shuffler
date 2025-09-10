module at.htlle.auk.shuffler {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    opens at.htlle.auk.shuffler to javafx.fxml;
    exports at.htlle.auk.shuffler;
    exports at.htlle.auk.shuffler.controller;
    opens at.htlle.auk.shuffler.controller to javafx.fxml;
}
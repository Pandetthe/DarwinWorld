module DarwinWorld {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.json;
    requires java.desktop;
    requires java.xml;
    requires jdk.unsupported;
    requires java.scripting;
    requires java.logging;
    requires java.naming;
    requires java.sql;
    
    opens agh.darwinworld to javafx.fxml, javafx.graphics;
    opens agh.darwinworld.presenters to javafx.fxml;
    opens agh.darwinworld.controls to javafx.fxml, javafx.graphics;
    opens agh.darwinworld.models to org.json;
    exports agh.darwinworld;
    exports agh.darwinworld.controls;
    exports agh.darwinworld.models;
}

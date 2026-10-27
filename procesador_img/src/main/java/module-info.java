module br.image {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires javafx.swing;
    requires java.desktop;

    opens br.image to javafx.fxml;

    exports br.image;
    exports br.image.entities;
}

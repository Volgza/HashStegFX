module com.hashsteg.hashstegfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hashsteg.hashstegfx to javafx.fxml;
    exports com.hashsteg.hashstegfx;
}
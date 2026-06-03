module terajournal {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires javafx.web;
	requires java.sql;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.platform.commons;
    requires org.junit.platform.launcher;


	opens com.tera13.application to javafx.graphics, javafx.fxml, org.junit.platform.commons;
	opens com.tera13.application.frontend.viewcontroller to javafx.fxml, javafx.graphics;
	opens com.tera13.application.frontend.view.gui to javafx.fxml, javafx.graphics;
	opens com.tera13.application.bean to javafx.fxml, javafx.graphics;
	opens com.tera13.application.backend.file to javafx.fxml, javafx.graphics, org.junit.platform.commons;
	opens com.tera13.application.backend to javafx.fxml, javafx.graphics, org.junit.platform.commons;
	exports com.tera13.application.frontend to javafx.graphics, javafx.fxml, org.junit.platform.commons;

}

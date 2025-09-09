module terajournal {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires javafx.web;
	requires java.sql;
    //requires org.junit.jupiter.api;
    //requires org.junit.jupiter.engine;
    //requires org.junit.platform.commons;
    //requires org.junit.platform.launcher;
	
	opens application to javafx.graphics, javafx.fxml;
	//opens tests to org.junit.platform.commons;
}

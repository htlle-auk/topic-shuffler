package at.htlle.auk.shuffler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Shuffler extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML and CSS
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/htlle/auk/shuffler/ShuffleView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        // load stylesheet if present
        try {
            String css = getClass().getResource("/at/htlle/auk/shuffler/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignore) { /* ignore if no stylesheet found */ }

        stage.setTitle("TopicShuffler");
        stage.setScene(scene);

        // Determine which screen to use (prefer the screen that currently contains the mouse pointer)
        Screen targetScreen = Screen.getPrimary();
        try {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            double mx = mouse.getX();
            double my = mouse.getY();
            for (Screen s : Screen.getScreens()) {
                Rectangle2D vb = s.getVisualBounds();
                if (vb.contains(mx, my)) {
                    targetScreen = s;
                    break;
                }
            }
        } catch (Throwable ignored) {
            // AWT may be unavailable in some environments; fall back to primary screen
            targetScreen = Screen.getPrimary();
        }

        // Compute a sane window size (percentage of the chosen screen's visual bounds)
        Rectangle2D visual = targetScreen.getVisualBounds();
        double screenW = visual.getWidth();
        double screenH = visual.getHeight();

        // Minimum sizes protect against too-small windows; percentages ensure the app fills most of the screen
        double desiredW = Math.max(800, screenW * 0.9); // at least 800px or 90% of screen width
        double desiredH = Math.max(600, screenH * 0.85); // at least 600px or 85% of screen height

        // Clamp to the actual visual bounds (avoid overflow on multi-monitor setups)
        desiredW = Math.min(desiredW, screenW);
        desiredH = Math.min(desiredH, screenH);

        // Center on the selected screen
        double posX = visual.getMinX() + (screenW - desiredW) / 2.0;
        double posY = visual.getMinY() + (screenH - desiredH) / 2.0;

        stage.setX(posX);
        stage.setY(posY);
        stage.setWidth(desiredW);
        stage.setHeight(desiredH);

        // Prevent the window from exceeding the screen size
        stage.setMaxWidth(screenW);
        stage.setMaxHeight(screenH);
        stage.setResizable(true);

        // Optional: set application icon if available (silent ignore if missing)
        try {
            InputStream is = getClass().getResourceAsStream("/at/htlle/auk/shuffler/images/app-icon.png");
            if (is != null) {
                javafx.scene.image.Image icon = new javafx.scene.image.Image(is);
                stage.getIcons().add(icon);
            }
        } catch (Exception ignored) { }

        // Finally show the stage
        stage.show();

        // Bring to front and request focus to ensure it's usable immediately
        stage.toFront();
        stage.requestFocus();
    }


    public static void main(String[] args) {
        launch();
    }
}





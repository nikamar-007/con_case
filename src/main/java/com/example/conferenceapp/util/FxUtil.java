package com.example.conferenceapp.util;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to keep JavaFX windows visually consistent across the app.
 */
public final class FxUtil {

    private static final String STYLESHEET = "/com/example/conferenceapp/css/styles.css";
    private static final String ICON_PNG  = "/com/example/conferenceapp/images/icon.png";
    private static final String ICON_ICO  = "/com/example/conferenceapp/images/icon.ico";

    private FxUtil() { }

    /** Attach the global Comic Sans + turquoise stylesheet to the scene if it is not already present. */
    public static void applyAppStyles(Scene scene) {
        if (scene == null) return;
        if (scene.getStylesheets().stream().anyMatch(s -> s.endsWith("styles.css"))) {
            return;
        }
        URL css = FxUtil.class.getResource(STYLESHEET);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    /** Apply the application icon (PNG fallback to ICO) to the given stage. */
    public static void applyAppIcon(Stage stage) {
        if (stage == null) return;
        URL iconUrl = FxUtil.class.getResource(ICON_PNG);
        if (iconUrl == null) {
            iconUrl = FxUtil.class.getResource(ICON_ICO);
        }
        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
        }
    }

    /**
     * Attempts to resolve and load a user photo from several known locations (working dir, import folders, resources).
     * The ImageView is cleared when the photo cannot be found.
     */
    public static void loadUserPhoto(ImageView view, String photoPath) {
        if (view == null) return;
        if (photoPath == null || photoPath.isBlank()) {
            view.setImage(null);
            return;
        }

        for (Path candidate : buildPhotoCandidates(photoPath)) {
            if (Files.exists(candidate)) {
                view.setImage(new Image(candidate.toUri().toString()));
                return;
            }
        }

        URL resource = FxUtil.class.getResource("/com/example/conferenceapp/images/" + photoPath);
        if (resource != null) {
            view.setImage(new Image(resource.toExternalForm()));
        } else {
            view.setImage(null);
        }
    }

    private static List<Path> buildPhotoCandidates(String photoPath) {
        List<Path> result = new ArrayList<>();
        Path direct = Paths.get(photoPath);
        if (direct.isAbsolute()) {
            result.add(direct);
            return result;
        }
        result.add(direct);
        result.add(Paths.get("data", "photos", photoPath));
        result.add(Paths.get("Сессия 1", "Участники_import", photoPath));
        result.add(Paths.get("Сессия 1", "Модераторы_import", photoPath));
        result.add(Paths.get("Сессия 1", "Жюри_import", photoPath));
        result.add(Paths.get("Сессия 1", "Организаторы_import", photoPath));
        return result;
    }
}

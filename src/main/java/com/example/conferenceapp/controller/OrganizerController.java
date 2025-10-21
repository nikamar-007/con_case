package com.example.conferenceapp.controller;

import com.example.conferenceapp.model.User;   // сделайте сущность пользователя
import com.example.conferenceapp.util.FxUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;

public class OrganizerController {

    @FXML private ImageView photo;
    @FXML private Label greeting;

    /* инициализация с данными пользователя */
    public void setUser(User u) {
        String partOfDay = partOfDay(LocalTime.now());
        greeting.setText(String.format("%s, %s %s!",
                partOfDay, u.getFirstName(), u.getMiddleName()));

        FxUtil.loadUserPhoto(photo, u.getPhotoPath());
    }

    private String partOfDay(LocalTime t) {
        if (t.isBefore(LocalTime.of(11,1)))      return "Доброе утро";
        if (t.isBefore(LocalTime.of(18,1)))      return "Добрый день";
        else                                     return "Добрый вечер";
    }

    /* статический фабричный метод */
    public static void open(User u) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    OrganizerController.class.getResource(
                            "/com/example/conferenceapp/fxml/Organizer.fxml"));
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Организатор: " + u.getFullName());

            Scene scene = new Scene(fx.load());
            FxUtil.applyAppStyles(scene);
            FxUtil.applyAppIcon(st);
            st.setScene(scene);

            OrganizerController c = fx.getController();
            c.setUser(u);

            st.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

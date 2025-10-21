package com.example.conferenceapp.controller;

import com.example.conferenceapp.model.Event;
import com.example.conferenceapp.util.FxUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DetailsController {

    /* UI */
    @FXML private ImageView logo;
    @FXML private Label title;
    @FXML private Label date;
    @FXML private Label time;
    @FXML private Label city;
    @FXML private Label organizer;
    @FXML private TextArea description;

    /* заполнение данными */
    public void setEvent(Event e) {
        FxUtil.loadEventLogo(logo, e.getLogoPath());

        title.setText(e.getTitle());
        if (e.getStart() != null) {
            date.setText(e.getStart().format(
                    DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        } else {
            date.setText("—");
        }
        time.setText(formatTime(e));
        city.setText(e.getCity() == null || e.getCity().isBlank() ? "—" : e.getCity());
        organizer.setText(e.getOrganizer() == null || e.getOrganizer().isBlank() ? "—" : e.getOrganizer());
        String text = e.getDescription();
        description.setText(text == null || text.isBlank() ? "Описание появится позже." : text);
    }

    private String formatTime(Event e) {
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        if (e.getStart() == null && e.getEnd() == null) {
            return "—";
        }
        String start = e.getStart() != null ? e.getStart().format(tf) : "??";
        String end = e.getEnd() != null ? e.getEnd().format(tf) : "??";
        return start + " – " + end;
    }

    /* статическая фабрика окна */
    public static void open(Event event) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    DetailsController.class.getResource(
                            "/com/example/conferenceapp/fxml/Details.fxml"));
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Информация о мероприятии");

            Scene scene = new Scene(fx.load());
            FxUtil.applyAppStyles(scene);
            FxUtil.applyAppIcon(st);
            st.setScene(scene);

            DetailsController c = fx.getController();
            c.setEvent(event);

            st.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

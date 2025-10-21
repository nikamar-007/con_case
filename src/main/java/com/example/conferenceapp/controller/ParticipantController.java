package com.example.conferenceapp.controller;

import com.example.conferenceapp.dao.ActivityDao;
import com.example.conferenceapp.model.Activity;
import com.example.conferenceapp.model.User;
import com.example.conferenceapp.util.FxUtil;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ParticipantController {

    @FXML private ImageView photo;
    @FXML private Label nameLabel;
    @FXML private Label directionLabel;
    @FXML private TableView<Activity> activityTable;
    @FXML private TableColumn<Activity, String> eventCol;
    @FXML private TableColumn<Activity, String> titleCol;
    @FXML private TableColumn<Activity, String> dateCol;
    @FXML private TableColumn<Activity, String> timeCol;
    @FXML private TableColumn<Activity, String> directionCol;

    private final ActivityDao activityDao = new ActivityDao();
    private User user;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public void initialize() {
        activityTable.setPlaceholder(new Label("Нет рекомендованных активностей"));
        eventCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEventTitle()));
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        directionCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDirection() == null ? "—" : c.getValue().getDirection()));
        dateCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(formatDate(c.getValue())));
        timeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(formatTime(c.getValue())));
    }

    public void setUser(User u) {
        this.user = u;
        nameLabel.setText(u.getFullName());
        directionLabel.setText(u.getDirectionName() != null ? u.getDirectionName() : "Все направления");
        FxUtil.loadUserPhoto(photo, u.getPhotoPath());
        loadActivities();
    }

    private void loadActivities() {
        Integer dirId = user != null ? user.getDirectionId() : null;
        int participantId = user != null ? user.getId() : 0;
        List<Activity> activities = activityDao.findForParticipant(participantId, dirId);
        activityTable.setItems(FXCollections.observableArrayList(activities));
    }

    private String formatDate(Activity activity) {
        if (activity.getEventDate() == null) {
            return "—";
        }
        String date = activity.getEventDate().format(dateFmt);
        if (activity.getDayNum() != null) {
            return date + " (День " + activity.getDayNum() + ")";
        }
        return date;
    }

    private String formatTime(Activity activity) {
        if (activity.getStartTime() == null && activity.getEndTime() == null) {
            return "—";
        }
        String start = activity.getStartTime() != null ? activity.getStartTime().format(timeFmt) : "??";
        String end   = activity.getEndTime()   != null ? activity.getEndTime().format(timeFmt)   : "??";
        return start + " – " + end;
    }

    @FXML
    private void onProfile() {
        if (user != null) {
            ProfileController.open(user);
        }
    }

    public static void open(User u) {
        try {
            FXMLLoader fx = new FXMLLoader(ParticipantController.class.getResource(
                    "/com/example/conferenceapp/fxml/Participant.fxml"));
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Участник: " + u.getFullName());

            Scene scene = new Scene(fx.load());
            FxUtil.applyAppStyles(scene);
            FxUtil.applyAppIcon(st);
            st.setScene(scene);

            ParticipantController controller = fx.getController();
            controller.setUser(u);

            st.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
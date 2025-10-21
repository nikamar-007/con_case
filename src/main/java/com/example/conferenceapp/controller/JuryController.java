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

public class JuryController {

    @FXML private ImageView photo;
    @FXML private Label nameLabel;
    @FXML private TableView<Activity> activityTable;
    @FXML private TableColumn<Activity, String> eventCol;
    @FXML private TableColumn<Activity, String> titleCol;
    @FXML private TableColumn<Activity, String> timeCol;
    @FXML private TableColumn<Activity, String> moderatorCol;

    private final ActivityDao activityDao = new ActivityDao();
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    private User user;

    public void initialize() {
        activityTable.setPlaceholder(new Label("Нет активностей для оценки"));
        eventCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEventTitle()));
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        moderatorCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getModerator() == null ? "—" : c.getValue().getModerator()));
        timeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(formatPeriod(c.getValue())));
    }

    public void setUser(User u) {
        this.user = u;
        nameLabel.setText(u.getFullName());
        FxUtil.loadUserPhoto(photo, u.getPhotoPath());
        loadActivities();
    }

    private void loadActivities() {
        List<Activity> activities = activityDao.findByJury(user.getId());
        activityTable.setItems(FXCollections.observableArrayList(activities));
    }

    private String formatPeriod(Activity activity) {
        StringBuilder sb = new StringBuilder();
        if (activity.getDayNum() != null) {
            sb.append("День ").append(activity.getDayNum());
        }
        if (activity.getStartTime() != null || activity.getEndTime() != null) {
            if (sb.length() > 0) sb.append(", ");
            String start = activity.getStartTime() != null ? activity.getStartTime().format(timeFmt) : "??";
            String end   = activity.getEndTime()   != null ? activity.getEndTime().format(timeFmt)   : "??";
            sb.append(start).append(" – ").append(end);
        }
        return sb.length() == 0 ? "—" : sb.toString();
    }

    public static void open(User u) {
        try {
            FXMLLoader fx = new FXMLLoader(JuryController.class.getResource(
                    "/com/example/conferenceapp/fxml/Jury.fxml"));
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Жюри: " + u.getFullName());

            Scene scene = new Scene(fx.load());
            FxUtil.applyAppStyles(scene);
            FxUtil.applyAppIcon(st);
            st.setScene(scene);

            JuryController controller = fx.getController();
            controller.setUser(u);

            st.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
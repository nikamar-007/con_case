package com.example.conferenceapp.model;

/**
 * Сущность пользователя системы.
 * <pre>
 *  id            — PK из БД
 *  idNumber      — отображаемый «ID Number»
 *  fullName      — Фамилия Имя Отчество одной строкой
 *  role          — роль (Participant / Moderator / Organizer / Jury)
 *  photoPath     — относительный или абсолютный путь к фотографии
 * </pre>
 */
public class User {

    public enum Role { PARTICIPANT, MODERATOR, ORGANIZER, JURY }

    private final int    id;
    private final String idNumber;
    private final String fullName;      // ФИО из БД (может быть null)
    private final String displayName;   // то, что показываем пользователю
    private final Role   role;
    private final String photoPath;
    private final Integer directionId;
    private final String directionName;

    public User(int id,
                String idNumber,
                String fullName,
                String displayName,
                Role role,
                String photoPath,
                Integer directionId,
                String directionName) {
        this.id        = id;
        this.idNumber  = idNumber;
        this.fullName  = fullName;
        this.displayName = displayName != null ? displayName : idNumber;
        this.role      = role;
        this.photoPath = photoPath;
        this.directionId = directionId;
        this.directionName = directionName;
    }

    /* ---------- getters ---------- */

    public int    getId()        { return id; }
    public String getIdNumber()  { return idNumber; }
    public String getFullName()  { return displayName; }
    public String getRawFullName() { return fullName; }
    public Role   getRole()      { return role; }
    public String getPhotoPath() { return photoPath; }
    public Integer getDirectionId() { return directionId; }
    public String getDirectionName() { return directionName; }

    /* ---------- «Имя» и «Отчество» из fullName ---------- */


    /** вернёт 2-е слово из ФИО либо всё ФИО, если пробелов нет */
    public String getFirstName() {
        String base = baseName();
        if (base.isEmpty()) return idNumber;
        String[] p = base.split("\\s+");
        return p.length > 1 ? p[1] : p[0];
    }

    /** вернёт 3-е слово (отчество) либо пустую строку */
    public String getMiddleName() {
        String base = baseName();
        if (base.isEmpty()) return "";
        String[] p = base.split("\\s+");
        return p.length > 2 ? p[2] : "";
    }

    private String baseName() {
        String candidate = fullName != null && !fullName.isBlank()
                ? fullName
                : displayName;
        return candidate != null ? candidate.trim() : "";
    }
}

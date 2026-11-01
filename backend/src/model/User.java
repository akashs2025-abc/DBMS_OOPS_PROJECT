package model;
import java.sql.Timestamp;

public class User {

    private int userId;
    private String fullName;
    private String normalizedName;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    public User() {
    }
    public User(String fullName, String normalizedName, String email, String passwordHash) {
        this.fullName = fullName;
        this.normalizedName = normalizedName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(int userId, String fullName, String normalizedName, String email, String passwordHash, Timestamp createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.normalizedName = normalizedName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", normalizedName='" + normalizedName + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

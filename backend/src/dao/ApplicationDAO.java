package dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import model.Application;
import util.InvalidStatusException;

/**
 * interfaces
 */
public interface ApplicationDAO {
    void addApplication(Application application) throws SQLException;
    void addApplicationForUser(Application application, int userId) throws SQLException;
    Application getApplicationById(int applicationId) throws SQLException;
    Application getApplicationByIdForUser(int applicationId, int userId) throws SQLException;
    List<Application> getAllApplications() throws SQLException;
    List<Application> getAllApplicationsForUser(int userId) throws SQLException;
    List<Application> searchByCompanyOrRole(String keyword) throws SQLException;
    List<Application> filterByStatus(String status) throws SQLException;
    List<Application> searchAndFilter(String keyword, String status, String type) throws SQLException;
    List<Application> searchAndFilterForUser(String keyword, String status, String type, int userId) throws SQLException;
    void updateApplication(Application application) throws SQLException;
    void updateApplicationForUser(Application application, int userId) throws SQLException;
    void updateStatus(int applicationId, String status) throws SQLException, InvalidStatusException;
    void updateStatusForUser(int applicationId, int userId, String status) throws SQLException, InvalidStatusException;
    void setFollowUpDate(int applicationId, LocalDate followUpDate) throws SQLException;
    void setFollowUpDateForUser(int applicationId, int userId, LocalDate followUpDate) throws SQLException;
    void deleteApplication(int applicationId) throws SQLException;
    void deleteApplicationForUser(int applicationId, int userId) throws SQLException;
}

package dao;

import java.sql.SQLException;
import java.util.List;
import model.Interview;

/**
 * data Access Object contract for Interview schedules and results.
 */
public interface InterviewDAO {
    void addInterview(Interview interview) throws SQLException;
    void addInterviewForUser(Interview interview, int userId) throws SQLException;
    List<Interview> getAllInterviews() throws SQLException;
    List<Interview> getAllInterviewsForUser(int userId) throws SQLException;
    List<Interview> getInterviewsByApplication(int applicationId) throws SQLException;
    List<Interview> getInterviewsByApplicationForUser(int applicationId, int userId) throws SQLException;
    void updateResult(int interviewId, String result) throws SQLException;
    void updateResultForUser(int interviewId, int userId, String result) throws SQLException;
    void deleteInterview(int interviewId) throws SQLException;
    void deleteInterviewForUser(int interviewId, int userId) throws SQLException;
}

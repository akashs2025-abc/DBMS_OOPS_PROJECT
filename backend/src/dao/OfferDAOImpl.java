package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Offer;
import util.DBConnection;

/**
 * JDBC Implementation for OfferDAO
 */
public class OfferDAOImpl implements OfferDAO {

    @Override
    public void addOffer(Offer offer) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO offers (application_id, package, deadline, offer_status) VALUES (?, ?, ?, ?)";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, offer.getApplicationId());
            ps.setDouble(2, offer.getPackageLpa());
            ps.setDate(3, Date.valueOf(offer.getDeadline()));
            ps.setString(4, offer.getOfferStatus() != null ? offer.getOfferStatus() : "Pending");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                offer.setOfferId(rs.getInt(1));
            }
        } finally {
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public void addOfferForUser(Offer offer, int userId) throws SQLException {
        String checkOwnerSql = "SELECT application_id FROM applications WHERE application_id = ? AND user_id = ? FOR UPDATE";
        String insertSql = "INSERT INTO offers (application_id, package, deadline, offer_status) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement owner = con.prepareStatement(checkOwnerSql)) {
                    owner.setInt(1, offer.getApplicationId());
                    owner.setInt(2, userId);
                    try (ResultSet rs = owner.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Application does not belong to this user.");
                        }
                    }
                }

                try (PreparedStatement insert = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insert.setInt(1, offer.getApplicationId());
                    insert.setDouble(2, offer.getPackageLpa());
                    insert.setDate(3, Date.valueOf(offer.getDeadline()));
                    insert.setString(4, offer.getOfferStatus() != null ? offer.getOfferStatus() : "Pending");
                    insert.executeUpdate();
                    try (ResultSet rs = insert.getGeneratedKeys()) {
                        if (rs.next()) {
                            offer.setOfferId(rs.getInt(1));
                        }
                    }
                }
                con.commit();
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        }
    }

    private static void rollback(Connection connection, SQLException cause) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            cause.addSuppressed(rollbackError);
        }
    }

    @Override
    public List<Offer> getAllOffers() throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Offer> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(a.role, ' - ', c.name) AS role_company " +
                     "FROM offers o " +
                     "INNER JOIN applications a ON o.application_id = a.application_id " +
                     "INNER JOIN companies c ON a.company_id = c.company_id " +
                     "ORDER BY o.package DESC";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Date d = rs.getDate("deadline");
                LocalDate deadline = (d != null) ? d.toLocalDate() : null;
                Offer off = new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("application_id"),
                        rs.getString("role_company"),
                        rs.getDouble("package"),
                        deadline,
                        rs.getString("offer_status")
                );
                list.add(off);
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public List<Offer> getAllOffersForUser(int userId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Offer> list = new ArrayList<>();
        String sql = "SELECT o.*, CONCAT(a.role, ' - ', c.name) AS role_company " +
                     "FROM offers o " +
                     "INNER JOIN applications a ON o.application_id = a.application_id " +
                     "INNER JOIN companies c ON a.company_id = c.company_id " +
                     "WHERE a.user_id = ? ORDER BY o.package DESC";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Date d = rs.getDate("deadline");
                LocalDate deadline = (d != null) ? d.toLocalDate() : null;
                list.add(new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("application_id"),
                        rs.getString("role_company"),
                        rs.getDouble("package"),
                        deadline,
                        rs.getString("offer_status")
                ));
            }
            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public Offer getOfferByApplication(int applicationId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT o.*, CONCAT(a.role, ' - ', c.name) AS role_company " +
                     "FROM offers o " +
                     "INNER JOIN applications a ON o.application_id = a.application_id " +
                     "INNER JOIN companies c ON a.company_id = c.company_id " +
                     "WHERE o.application_id = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, applicationId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Date d = rs.getDate("deadline");
                LocalDate deadline = (d != null) ? d.toLocalDate() : null;
                return new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("application_id"),
                        rs.getString("role_company"),
                        rs.getDouble("package"),
                        deadline,
                        rs.getString("offer_status")
                );
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public Offer getOfferByApplicationForUser(int applicationId, int userId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT o.*, CONCAT(a.role, ' - ', c.name) AS role_company " +
                     "FROM offers o " +
                     "INNER JOIN applications a ON o.application_id = a.application_id " +
                     "INNER JOIN companies c ON a.company_id = c.company_id " +
                     "WHERE o.application_id = ? AND a.user_id = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, applicationId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Date d = rs.getDate("deadline");
                LocalDate deadline = (d != null) ? d.toLocalDate() : null;
                return new Offer(
                        rs.getInt("offer_id"),
                        rs.getInt("application_id"),
                        rs.getString("role_company"),
                        rs.getDouble("package"),
                        deadline,
                        rs.getString("offer_status")
                );
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public void updateOfferStatus(int offerId, String status) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE offers SET offer_status = ? WHERE offer_id = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, offerId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public void updateOfferStatusForUser(int offerId, int userId, String status) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE offers o " +
                     "INNER JOIN applications a ON o.application_id = a.application_id " +
                     "SET o.offer_status = ? WHERE o.offer_id = ? AND a.user_id = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, offerId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public void deleteOffer(int offerId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "DELETE FROM offers WHERE offer_id = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, offerId);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            DBConnection.closeConnection(con);
        }
    }

    @Override
    public void deleteOfferForUser(int offerId, int userId) throws SQLException {
        String findOwnedSql = "SELECT o.application_id FROM offers o " +
                              "INNER JOIN applications a ON o.application_id = a.application_id " +
                              "WHERE o.offer_id = ? AND a.user_id = ? FOR UPDATE";
        String deleteSql = "DELETE FROM offers WHERE offer_id = ? AND application_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int applicationId;
                try (PreparedStatement findOwned = con.prepareStatement(findOwnedSql)) {
                    findOwned.setInt(1, offerId);
                    findOwned.setInt(2, userId);
                    try (ResultSet rs = findOwned.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Offer does not belong to this user.");
                        }
                        applicationId = rs.getInt(1);
                    }
                }

                try (PreparedStatement delete = con.prepareStatement(deleteSql)) {
                    delete.setInt(1, offerId);
                    delete.setInt(2, applicationId);
                    if (delete.executeUpdate() != 1) {
                        throw new SQLException("Offer could not be deleted.");
                    }
                }
                con.commit();
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        }
    }
}

package dao;

import java.sql.SQLException;
import java.util.List;
import model.Offer;

/**
Data Access Object contract for Job and Internship Offers.
 */
public interface OfferDAO {
    void addOffer(Offer offer) throws SQLException;
    void addOfferForUser(Offer offer, int userId) throws SQLException;
    List<Offer> getAllOffers() throws SQLException;
    List<Offer> getAllOffersForUser(int userId) throws SQLException;
    Offer getOfferByApplication(int applicationId) throws SQLException;
    Offer getOfferByApplicationForUser(int applicationId, int userId) throws SQLException;
    void updateOfferStatus(int offerId, String status) throws SQLException;
    void updateOfferStatusForUser(int offerId, int userId, String status) throws SQLException;
    void deleteOffer(int offerId) throws SQLException;
    void deleteOfferForUser(int offerId, int userId) throws SQLException;
}

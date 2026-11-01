package dao;

import java.sql.SQLException;
import java.util.List;
import model.Company;

/**
 * interfaces & Packages
 */
public interface CompanyDAO {
    void addCompany(Company company) throws SQLException;
    Company getCompanyById(int companyId) throws SQLException;
    List<Company> getAllCompanies() throws SQLException;
    void updateCompany(Company company) throws SQLException;
    void deleteCompany(int companyId) throws SQLException;
}

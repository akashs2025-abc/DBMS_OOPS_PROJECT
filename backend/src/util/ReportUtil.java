package util;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Application;
import model.Offer;

public class ReportUtil {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            "Applied", "Shortlisted", "Interview Scheduled", "Offered", "Rejected"
    ));

    public static void validateStatus(String status) throws InvalidStatusException {
        if (status == null || !VALID_STATUSES.contains(status.trim())) {
            throw new InvalidStatusException("Invalid application status: '" + status 
                    + "'. Allowed: " + VALID_STATUSES);
        }
    }

    public static List<Application> filterApplications(List<Application> applications,Predicate<Application> condition) {
        return applications.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static List<Application> filterByStatus(List<Application> applications, String status) {
        return filterApplications(applications, app -> app.getStatus().equalsIgnoreCase(status));
    }

    public static List<Application> getUpcomingFollowUps(List<Application> applications, int days) {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(days);

        return applications.stream()
                .filter(app -> app.getFollowUpDate() != null)
                .filter(app -> !app.getFollowUpDate().isBefore(today) && !app.getFollowUpDate().isAfter(maxDate))
                .sorted(Comparator.comparing(Application::getFollowUpDate))
                .collect(Collectors.toList());
    }

    public static List<Application> getOverdueFollowUps(List<Application> applications) {
        LocalDate today = LocalDate.now();

        return applications.stream()
                .filter(app -> app.getFollowUpDate() != null)
                .filter(app -> app.getFollowUpDate().isBefore(today))
                .sorted(Comparator.comparing(Application::getFollowUpDate))
                .collect(Collectors.toList());
    }

    
    public static Map<String, Long> getApplicationsGroupedByStatus(List<Application> applications) {
        return applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));
    }

    public static Map<String, Long> getApplicationsPerCompany(List<Application> applications) {
        return applications.stream()
                .collect(Collectors.groupingBy(
                        app -> (app.getCompanyName() != null ? app.getCompanyName() : "Unknown"),
                        Collectors.counting()
                ));
    }

    public static OptionalDouble getHighestPackage(List<Offer> offers) {
        return offers.stream()
                .mapToDouble(Offer::getPackageLpa)
                .max();
    }

    public static double calculateOfferRate(int totalOffers, int totalApplications) {
        if (totalApplications == 0) return 0.0;
        return ((double) totalOffers / totalApplications) * 100.0;
    }

    public static void printSummaryReport(Map<String, Long> statusCounts) {
        System.out.println("--- Status Breakdown ---");
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            System.out.println(" * " + entry.getKey() + ": " + entry.getValue());
        }
    }
}

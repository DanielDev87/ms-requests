package co.com.bancolombia.r2dbc.config;

public class DBQueries {
    public static final String FIND_APPLICATIONS_FOR_REVIEW = """
        SELECT
            la.document_number,
            la.amount,
            la.term,
            la.status,
            lt.name AS loan_type_name,
            lt.interest_rate
        FROM
            loan_applications la
        JOIN
            loan_types lt ON la.loan_type_id = lt.id
        WHERE
            la.status IN (:statuses)
        ORDER BY
            la.request_date DESC
        LIMIT :size OFFSET :offset
    """;

    private DBQueries() {
    }
}

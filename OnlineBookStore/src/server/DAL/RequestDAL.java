package server.DAL;

import server.BLL.DatabaseService;
import server.model.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RequestDAL {

    public synchronized List<Request> getAcceptedRequests() {
        return getRequestsByStatus("Accepted");
    }

    public synchronized List<Request> getPendingRequests() {
        return getRequestsByStatus("Pending");
    }

    public synchronized List<Request> getRejectedRequests() {
        return getRequestsByStatus("Rejected");
    }

    private synchronized List<Request> getRequestsByStatus(String status) {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM Requests WHERE status = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int lenderId = rs.getInt("lender_id");
                    int bookId = rs.getInt("book_id");
                    Request request = new Request(borrowerId, lenderId, bookId);
                    request.setId(id);
                    request.setStatus(status);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }


    // Get borrowing requests for a specific user as a borrower
    public synchronized List<Request> getRequestsForBorrower(int borrowerId) {
        List<Request> borrowerRequests = new ArrayList<>();
        String getPendingRequestsSQL = "SELECT * FROM Requests WHERE borrower_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getPendingRequestsSQL)) {
            pstmt.setInt(1, borrowerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int lenderId = rs.getInt("lender_id");
                    int bookId = rs.getInt("book_id");
                    String status = rs.getString("status");
                    Request request = new Request(borrowerId, lenderId, bookId);
                    request.setId(id);
                    request.setStatus(status);
                    borrowerRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowerRequests;
    }

    public synchronized List<Request> getRequestsForLender(int lenderId) {
        List<Request> lenderRequests = new ArrayList<>();
        String getRequestsSQL = "SELECT * FROM Requests WHERE lender_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getRequestsSQL)) {
            pstmt.setInt(1, lenderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int bookId = rs.getInt("book_id");
                    String status = rs.getString("status");
                    Request request = new Request(borrowerId, lenderId, bookId);
                    request.setId(id);
                    request.setStatus(status);
                    lenderRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lenderRequests;
    }

    // Add a borrowing request to the database
    public synchronized void addRequest(Request request) throws SQLException {
        String addRequestSQL = "INSERT INTO Requests (borrower_id, lender_id, book_id, status) VALUES (?, ?, ?, 'pending')";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(addRequestSQL)) {
            pstmt.setInt(1, request.getBorrowerId());
            pstmt.setInt(2, request.getLenderId());
            pstmt.setInt(3, request.getBookId());
            pstmt.executeUpdate();
            System.out.println("Request added to the database.");
        } catch (SQLException e) {
            System.out.println("Error adding request to the database: " + e.getMessage());
            throw e;
        }
    }

    // Get pending borrowing requests for a specific user
    public synchronized List<Request> getPendingRequestsForLender(int userId) {
        List<Request> pendingRequests = new ArrayList<>();
        String getPendingRequestsSQL = "SELECT * FROM Requests WHERE lender_id = ? AND status = 'Pending'";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getPendingRequestsSQL)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int lenderId = rs.getInt("lender_id");
                    int bookId = rs.getInt("book_id");
                    String status = rs.getString("status");
                    Request request=new Request(borrowerId, lenderId, bookId);
                    request.setId(id);
                    request.setStatus(status);
                    pendingRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingRequests;
    }
    public synchronized List<Request> getAcceptedRequestsForRequester(int userId) {
        List<Request> AcceptedRequests = new ArrayList<>();
        String getRequestsSQL = "SELECT * FROM Requests WHERE borrower_id = ? AND status = 'Accepted'";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getRequestsSQL)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int bookId = rs.getInt("book_id");
                    //String status = rs.getString("status");
                    Request request=new Request(borrowerId, userId, bookId);
                    request.setId(id);
                    request.setStatus("Accpeted");
                    AcceptedRequests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return AcceptedRequests;
    }

    // Get all borrowing requests for a specific user
    public synchronized List<Request> getAllRequestsForUser(int userId) {
        List<Request> allRequests = new ArrayList<>();
        String getAllRequestsSQL = "SELECT * FROM Requests WHERE lender_id = ? OR borrower_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getAllRequestsSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int lenderId = rs.getInt("lender_id");
                    int bookId = rs.getInt("book_id");
                    String status = rs.getString("status");
                    Request request=new Request(borrowerId, lenderId, bookId);
                    request.setId(id);
                    request.setStatus(status);
                    allRequests.add(request);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allRequests;
    }

    // Accept a borrowing request
    public synchronized void acceptRequest(int requestId) {
        updateRequestStatus(requestId, "Accepted");
    }

    // Reject a borrowing request
    public synchronized void rejectRequest(int requestId) {
        updateRequestStatus(requestId, "Rejected");
    }

    private synchronized void updateRequestStatus(int requestId, String status) {
        String updateRequestStatusSQL = "UPDATE Requests SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateRequestStatusSQL)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);

            pstmt.executeUpdate();
            System.out.println("Request status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Request getRequest(int requestId) {
        String getPendingRequestsSQL = "SELECT * FROM Requests WHERE id = ?";
        Request request = null;

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getPendingRequestsSQL)) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int borrowerId = rs.getInt("borrower_id");
                    int lenderId = rs.getInt("lender_id");
                    int bookId = rs.getInt("book_id");
                    String status = rs.getString("status");

                    request=new Request(borrowerId, lenderId, bookId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return request;
    }
}

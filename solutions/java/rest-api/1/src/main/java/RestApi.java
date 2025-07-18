import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestApi {

    private List<User> users = new ArrayList<>();

    public RestApi(User... users) {
        this.users.addAll(List.of(users));
    }

    public String get(String url) {
        return handleGetAllUsers();
    }

    public String get(String url, JSONObject payload) {
        switch (url) {
            case "/users":
                return handleGetSpecificUsers(payload);
            default:
        }
        return null;
    }

    public String post(String url, JSONObject payload) {
        switch (url) {
            case "/add":
                return handleAddUser(payload);
            case "/iou":
                return handleIou(payload);
            default:
        }
        return null;
    }

    private String handleGetAllUsers() {
        JSONArray usersArray = new JSONArray();
        for (User user : users) {
            usersArray.put(userJson(user));
        }
        return new JSONObject().put("users", usersArray).toString();
    }

    private String handleGetSpecificUsers(JSONObject payload) {
        JSONArray usersArray = new JSONArray();
        List<String> requestedUsers = payload.getJSONArray("users").toList().stream()
            .map(Object::toString)
            .toList();
        
        for (User user : users) {
            if (requestedUsers.contains(user.name())) {
                usersArray.put(userJson(user));
            }
        }
        return new JSONObject().put("users", usersArray).toString();
    }

    private String handleAddUser(JSONObject payload) {
        final User user = userFromPayload(payload);
        users.add(user);
        return userJson(user).toString();
    }

    private String handleIou(JSONObject payload) {
        final String lenderName = payload.getString("lender");
        final String borrowerName = payload.getString("borrower");
        final double amount = payload.getDouble("amount");

        // Find existing users or create new ones
        User lender = getUser(lenderName).orElse(User.builder().setName(lenderName).build());
        User borrower = getUser(borrowerName).orElse(User.builder().setName(borrowerName).build());

        // Update users with new IOU
        lender = updateUserWithIou(lender, borrowerName, amount, true);
        borrower = updateUserWithIou(borrower, lenderName, amount, false);

        // Update the users list
        updateUserInList(lender);
        updateUserInList(borrower);

        return createIouResponse(lender, borrower);
    }

    private String createIouResponse(User lender, User borrower) {
        // Sort users alphabetically by name
        List<User> sortedUsers = List.of(lender, borrower).stream()
            .sorted((u1, u2) -> u1.name().compareTo(u2.name()))
            .toList();
        
        JSONArray usersArray = new JSONArray();
        for (User sortedUser : sortedUsers) {
            usersArray.put(userJson(sortedUser));
        }
        return new JSONObject().put("users", usersArray).toString();
    }

    private Optional<User> getUser(String name) {
        return users.stream().filter(u -> u.name().equals(name)).findFirst();
    }

    private User updateUserWithIou(User user, String otherUserName, double amount, boolean isLender) {
        User.Builder builder = User.builder().setName(user.name());
        
        // Copy existing owes (excluding the other user)
        for (Iou iou : user.owes()) {
            if (!iou.name.equals(otherUserName)) {
                builder.owes(iou.name, iou.amount);
            }
        }
        
        // Copy existing owedBy (excluding the other user)
        for (Iou iou : user.owedBy()) {
            if (!iou.name.equals(otherUserName)) {
                builder.owedBy(iou.name, iou.amount);
            }
        }
        
        // Calculate net amount for this user with the other user
        double currentOwes = user.owes().stream()
            .filter(iou -> iou.name.equals(otherUserName))
            .mapToDouble(iou -> iou.amount)
            .sum();
        
        double currentOwedBy = user.owedBy().stream()
            .filter(iou -> iou.name.equals(otherUserName))
            .mapToDouble(iou -> iou.amount)
            .sum();
        
        double netAmount;
        if (isLender) {
            // User is lending to other user
            netAmount = currentOwedBy + amount - currentOwes;
        } else {
            // User is borrowing from other user
            netAmount = currentOwes + amount - currentOwedBy;
        }
        
        // Add the net relationship (only if non-zero)
        if (netAmount > 0) {
            if (isLender) {
                builder.owedBy(otherUserName, netAmount);
            } else {
                builder.owes(otherUserName, netAmount);
            }
        } else if (netAmount < 0) {
            if (isLender) {
                builder.owes(otherUserName, -netAmount);
            } else {
                builder.owedBy(otherUserName, -netAmount);
            }
        }
        // If netAmount is 0, no relationship is added
        
        return builder.build();
    }

    private void updateUserInList(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).name().equals(updatedUser.name())) {
                users.set(i, updatedUser);
                return;
            }
        }
        users.add(updatedUser);
    }

    private static User userFromPayload(JSONObject payload) {
        return User.builder()
            .setName(payload.getString("user"))
            .build();
    }

    private static JSONObject userJson(User user) {
        return new JSONObject()
            .put("name", user.name())
            .put("owes", owesJson(user.owes()))
            .put("owedBy", owedByJson(user.owedBy()))
            .put("balance", calculateBalance(user));
    }

    private static JSONObject owesJson(List<Iou> owes) {
        JSONObject result = new JSONObject();
        for (Iou iou : owes) {
            result.put(iou.name, iou.amount);
        }
        return result;
    }

    private static JSONObject owedByJson(List<Iou> owedBy) {
        JSONObject result = new JSONObject();
        for (Iou iou : owedBy) {
            result.put(iou.name, iou.amount);
        }
        return result;
    }

    private static double calculateBalance(User user) {
        double balance = 0.0;
        for (Iou iou : user.owedBy()) {
            balance += iou.amount;
        }
        for (Iou iou : user.owes()) {
            balance -= iou.amount;
        }
        return balance;
    }
}

package DB;

/**
 * @deprecated Use {@link org.example.model.User} instead.
 */
@Deprecated
public class User extends org.example.model.User {
    public User() {
        super();
    }

    public User(String username, String password, String email) {
        super(username, password, email);
    }
}

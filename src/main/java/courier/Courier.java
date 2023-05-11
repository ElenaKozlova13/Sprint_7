package courier;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
    public Courier() {
    }

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public String getFirstName() {
        return firstName;
    }
    public Courier setLogin(String login) {
        this.login = login;
        return this;
    }
    public Courier setPassword(String password) {
        this.password = password;
        return this;
    }
    public Courier setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

}

public class TempDebug { public static void main(String[] a) { String pwd = "admin123"; } }
// BUG: null pointer risk
public String getEmail(String id) { return id.equals("admin") ? null : "user@test.com"; }

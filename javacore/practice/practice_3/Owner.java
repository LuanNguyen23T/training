package javacore.practice.practice_3;

import java.util.Objects;
import java.util.regex.Pattern;

public class Owner {
    private String cccd;
    private String fullName;
    private String email;

    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Owner() {
    }

    public Owner(String cccd, String fullName, String email) {
        setCccd(cccd);
        setFullName(fullName);
        setEmail(email);
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        if (cccd == null || !CCCD_PATTERN.matcher(cccd.trim()).matches()) {
            throw new IllegalArgumentException("Số CCCD phải chứa đúng 12 chữ số!");
        }
        this.cccd = cccd.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên chủ xe không được để trống!");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng!");
        }
        this.email = email.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return Objects.equals(cccd, owner.cccd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cccd);
    }

    @Override
    public String toString() {
        return String.format("[CCCD: %s | Chủ xe: %s | Email: %s]", cccd, fullName, email);
    }
}

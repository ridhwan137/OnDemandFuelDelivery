package my.edu.utem.ftmk.ondemandfueldelivery;

public class User {

    String Address, Email, FullName, Password, PhoneNum, PictureURL, RequestDate, RequestTime,
    accountStatus, userPictureIC, userSelfieWithIC, userType;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getPictureURL() {
        return PictureURL;
    }

    public void setPictureURL(String pictureURL) {
        PictureURL = pictureURL;
    }

    public String getRequestDate() {
        return RequestDate;
    }

    public void setRequestDate(String requestDate) {
        RequestDate = requestDate;
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getUserPictureIC() {
        return userPictureIC;
    }

    public void setUserPictureIC(String userPictureIC) {
        this.userPictureIC = userPictureIC;
    }

    public String getUserSelfieWithIC() {
        return userSelfieWithIC;
    }

    public void setUserSelfieWithIC(String userSelfieWithIC) {
        this.userSelfieWithIC = userSelfieWithIC;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}

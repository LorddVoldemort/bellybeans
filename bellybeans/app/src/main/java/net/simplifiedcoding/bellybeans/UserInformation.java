package net.simplifiedcoding.bellybeans;

/**
 * Created by indian on 3/7/2018.
 */

public class UserInformation {
    private String ID;
    private String Name;
    private String Address;
    private String PhoneNumber;
    private String Email;
    private String AccNum;
    private String CardNum;

    public UserInformation()
    {

    }

    public UserInformation(String ID,String name, String address, String phoneNumber, String email, String accNum, String cardNum) {
        ID=ID;
        Name = name;
        Address = address;
        PhoneNumber = phoneNumber;
        Email = email;
        AccNum = accNum;
        CardNum = cardNum;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }


    public String getAddress() {
        return Address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public String getAccNum() {
        return AccNum;
    }

    public String getCardNum() {
        return CardNum;
    }


}

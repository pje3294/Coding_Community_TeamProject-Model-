package model.users;

/*
 * user_num int primary key,
user_name varchar(15) not null,
user_id varchar(50) not null,
user_pw varchar(50) not null,
user_hp varchar(25) not null,
user_gender varchar(5) not null,
user_email varchar(225) not null,
user_addr varchar(225) not null,
user_birth varchar(30) not null,
icon_id varchar(30) not null
 * */

public class UsersVO {

	private int userNum;
	private String name;
	private String id;
	private String pw;
	private String phone;
	private String gender;
	private String email;
	private String addr;
	private String birth;
	private String iconId;

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	@Override
	public String toString() {
		return "UsersVO [userNum=" + userNum + ", name=" + name + ", id=" + id + ", pw=" + pw + ", phone=" + phone
				+ ", gender=" + gender + ", email=" + email + ", addr=" + addr + ", birth=" + birth + ", iconId="
				+ iconId + "]";
	}

}

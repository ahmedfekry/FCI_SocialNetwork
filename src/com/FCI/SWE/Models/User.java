package com.FCI.SWE.Models;

import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class User {
	//private long id;
	private String name;
	private String email;
	private String password;
	private Vector<Integer>postsIDs;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Vector<Integer> getPostsIDs() {
		return postsIDs;
	}

	public void setPostsIDs(Vector<Integer> postsIDs) {
		this.postsIDs = postsIDs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private static User currentActiveUser;

	/**
	 * Constructor accepts user data
	 * 
	 * @param name
	 *            user name
	 * @param email
	 *            user email
	 * @param password
	 *            user provided password
	 */
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;

	}
	
	/*private void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}*/

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return password;
	}
	
	public static User getCurrentActiveUser(){
		return currentActiveUser;
	}
	
	/**
	 * 
	 * This static method will form UserEntity class using json format contains
	 * user data
	 * 
	 * @param json
	 *            String in json format contains user data
	 * @return Constructed user entity
	 */
	public static User getUser(String json) {

		JSONParser parser = new JSONParser();
		try {
			JSONObject object = (JSONObject) parser.parse(json);
			currentActiveUser = new User(object.get("name").toString(), object.get(
					"email").toString(), object.get("password").toString());
			//currentActiveUser.setId(Long.parseLong(object.get("id").toString()));
			return currentActiveUser;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
		
	public static void signOut(){
		currentActiveUser = null;
	}

}
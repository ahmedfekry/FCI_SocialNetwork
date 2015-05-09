package com.FCI.SWE.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.FCI.SWE.Models.Comment;
import com.FCI.SWE.Models.PostType;
import com.FCI.SWE.Models.User;
import com.FCI.SWE.ServicesModels.UserEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;


@Path("/")
@Produces("text/html")
public class PostServices {
	/**
	 * 
	 * @param owner username of user wants to create the post
	 * @param password user password
	 * @param post the post to be published
	 * @return JSON Object of Status OK or Failed and reason
	 */
	@Path("createUserPost")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String createUserPost(@FormParam("owner")String owner, @FormParam("password")String password,
			@FormParam("post")String post, @FormParam("privacy")String privacy,
			@FormParam("feeling")String feeling
			)
	{
		JSONObject object = new JSONObject();
		
		User user = UserEntity.getUser(owner, password);
		
		if(post.equals("") )
		{
			object.put("Status", "Failed, empty post");
			return object.toJSONString();
		}
		
		if(user == null)
		{
			object.put("Status", "Failed, not authorized user ");
			return object.toJSONString();
		}
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query gaeQuery = new Query("Posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		long id =1;
		if(list.size()!=0)
			id =list.get(list.size()-1).getKey().getId()+ 1;
		
		Entity pos = new Entity("Posts", id);
		pos.setProperty("owner", owner);
		pos.setProperty("post", post);
		pos.setProperty("privacy", privacy);
		pos.setProperty("date", new Date());
		pos.setProperty("type", PostType.Personal.toString());
		pos.setProperty("id", id);
		pos.setProperty("likes", new Vector<String>());
		pos.setProperty("comments", new Vector<Comment>());
		pos.setProperty("feeling", feeling);
		
		datastore.put(pos);
		/////////////////////////
		
		Query geo = new Query("users");
		PreparedQuery prepare = datastore.prepare(geo);
		List<Entity> usersList = prepare.asList(FetchOptions.Builder.withDefaults());
		
		for(Entity entity: usersList){
			
			String ownerUser = entity.getProperty("name").toString();			
			
			if( ownerUser.equals(owner) ){
				
				Vector<String>posts;
				Object obj = entity.getProperty("posts");
				
				if(obj == null)
					posts = new Vector<String>();	
				else
					posts = new Vector<String>((ArrayList<String> )obj);
				
				posts.insertElementAt(String.valueOf(id), 0);
				
				entity.setProperty("posts", posts);
				
				datastore.put(entity);
				
				break;
			}
		}
		
		// check on privacy and add the post id to users
		
		object.put("Status", "OK");
		return object.toJSONString();
	}
	
	////////////////////////////////////////////////////////////////////////////
	@Path("getUserPosts")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserPosts(@FormParam("owner")String owner, @FormParam("password")String password)
	{
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		
		User user = UserEntity.getUser(owner, password);
		
		if(user == null)
		{
			object.put("Status", "Failed, not authorized user ");
			array.add(object);
			return array.toJSONString();
		}
		
		object.put("Status", "OK ");
		array.add(object);
		
		
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		
		Query gaeQuery = new Query("Posts");
		PreparedQuery pq = datastore.prepare(gaeQuery);
		
		List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
		
		for(Entity entity : list)
		{
			if(entity.getProperty("owner").equals(owner))
			{
				Vector<String>likes;
				if(entity.getProperty("likes")!= null)
					likes = new Vector<String>((ArrayList<String>)entity.getProperty("likes"));
				else 
					likes = new Vector<String>();
				
				Vector<Comment>comments;
				if(entity.getProperty("comments")!= null)
					comments = new Vector<Comment>((ArrayList<Comment>)entity.getProperty("comments"));
				else 
					comments = new Vector<Comment>();
				
				
				JSONObject obj = new JSONObject();
				obj.put("owner", entity.getProperty("owner").toString());
				obj.put("post", entity.getProperty("post").toString());
				obj.put("privacy", entity.getProperty("privacy").toString());
				obj.put("date", entity.getProperty("date").toString());
				obj.put("type", entity.getProperty("type").toString());
				obj.put("id", entity.getProperty("id").toString());
				obj.put("likes", likes);
				obj.put("comments", comments);
				obj.put("feeling", entity.getProperty("feeling").toString());
				
				array.add(obj);
			}
		}
		
		
		
		return array.toJSONString();
	}
	
}